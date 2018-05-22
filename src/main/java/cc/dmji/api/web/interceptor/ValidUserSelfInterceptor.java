package cc.dmji.api.web.interceptor;

import cc.dmji.api.annotation.ValidUserSelf;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by echisan on 2018/5/22
 */
public class ValidUserSelfInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ValidUserSelfInterceptor.class);

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        HandlerMethod handlerMethod = null;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        } else {
            return true;
        }
        Method method = handlerMethod.getMethod();
        ValidUserSelf validUserSelfAnnotation = method.getAnnotation(ValidUserSelf.class);

        if (validUserSelfAnnotation == null) {
            return true;
        }
        // 如果有这个注解
        logger.info("valid user self interceptor");

        String token = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION)
                .replace(SecurityConstants.TOKEN_PREFIX, "");

        JwtTokenUtils.Payload payload = jwtTokenUtils.getPayload(token);
        // 如果是非普通用户则放行
        if (payload.getRole().equals(Role.MANAGER.getName())
                || payload.getRole().equals(Role.ADMIN.getName())) {
            return true;
        }

        // 验证是否是本人操作
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        logger.info("url [{}], uri [{}]", url, uri);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String userId = uri.substring(uri.lastIndexOf("/") + 1);
        User user = userService.getUserById(userId);
        if (user == null){
            ObjectMapper objectMapper = new ObjectMapper();
            Result result = new Result(ResultCode.PARAM_IS_INVALID);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }
        if (user.getNick().equals(payload.getUsername())){
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().writeValueAsString(new Result<>().setResultCode(ResultCode.PERMISSION_DENY)));
        return false;
    }
}
