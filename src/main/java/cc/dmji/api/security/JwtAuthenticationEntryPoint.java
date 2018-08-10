package cc.dmji.api.security;

import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by echisan on 2018/5/19
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        StringBuffer url = request.getRequestURL();
        String header = response.getHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<String> result = new Result<String>(ResultCode.PERMISSION_DENY);
        String msg = null;
        if (header!=null){
            Integer code = Integer.valueOf(header);
            ResultCode resultCode = ResultCode.byCode(code);
            result.setResultCode(resultCode);
            if (code.equals(ResultCode.USER_ACCOUNT_FORBIDDEN.getCode())){
                Long lockTime = (Long) request.getAttribute("ts");
                request.removeAttribute("ts");
                Timestamp ts = new Timestamp(lockTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                msg = "该帐号已被封禁，解封日期为【"+sdf.format(ts)+"】具体原因请联系管理员";
            }
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        result.setData(url.toString());

//        String msg = ResultCode.getMsg(code);
//        if (msg != null){
//            result.setMsg(msg);
//        }
        if (msg!=null){
            result.setMsg(msg);
        } else {
            result.setMsg(authException.getMessage());
        }
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
