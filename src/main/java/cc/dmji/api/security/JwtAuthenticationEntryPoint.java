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

/**
 * Created by echisan on 2018/5/19
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        StringBuffer url = request.getRequestURL();
        Integer code = Integer.valueOf(response.getHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<String> result = new Result<String>(ResultCode.PERMISSION_DENY);
        result.setData(url.toString());
//        String msg = ResultCode.getMsg(code);
//        if (msg != null){
//            result.setMsg(msg);
//        }
        result.setMsg(authException.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
