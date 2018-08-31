package cc.dmji.api.web;

import cc.dmji.api.annotation.RequestLimit;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RequestLimitInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLimitInterceptor.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequestLimit requestLimit = handlerMethod.getMethodAnnotation(RequestLimit.class);
            if (requestLimit == null) {
                return true;
            }
            String ipAddress = GeneralUtils.getIpAddress(request);
            String methodName = handlerMethod.getMethod().getName();
            String redisKey = null;
            // 先判断是否为登录用户
            String header = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
            if (header != null && header.startsWith(SecurityConstants.TOKEN_PREFIX)){
                String token = header.replace(SecurityConstants.TOKEN_PREFIX,"");
                try {
                    Long uid = jwtTokenUtils.getUid(token);
                    redisKey = getRedisKey(methodName, String.valueOf(uid));
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(new ObjectMapper().writeValueAsString(new Result<>(ResultCode.PERMISSION_DENY)));
                    return false;
                }
            }
            if (redisKey == null){
                redisKey = getRedisKey(methodName, ipAddress);
            }

            BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps(redisKey);
            // 如果不为空，则说明限制了请求
            if (ops.get() != null){
                String value = requestLimit.value();
                Result result = new Result(ResultCode.USER_REQUEST_FREQUENTLY);
                if (!value.equals("")){
                    result.setMsg(value);
                }
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                return false;
            }

            ops.set("request limit", Long.parseLong(requestLimit.timeout()),requestLimit.timeUnit());
            return true;
        }
        return true;
    }

    private String getRedisKey(String methodName, String ipAddress) {
        return RedisKey.REQUEST_LIMIT + methodName + ipAddress;
    }
}
