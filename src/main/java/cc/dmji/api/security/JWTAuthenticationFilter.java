package cc.dmji.api.security;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.LoginRecord;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.UserStatus;
import cc.dmji.api.service.LoginRecordService;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.web.model.AuthUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static cc.dmji.api.constants.SecurityConstants.TOKEN_HEADER_AUTHORIZATION;
import static cc.dmji.api.constants.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by echisan on 2018/5/18
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private static final Integer REMEMBER = 1;
    private static final Integer UN_REMEMBER = 0;

    @Autowired
    private RedisTokenService redisTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginRecordService loginRecordService;

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public void setUsernameParameter(String usernameParameter) {
        super.setUsernameParameter("principal");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            AuthUser user = new ObjectMapper()
                    .readValue(request.getInputStream(), AuthUser.class);
            request.setAttribute("remember_me", user.getRemember_me());
            return super.getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getPrincipal(),
                            user.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            logger.info("不能从request中读取到相应的数据,{}", e.getMessage());
            setResponse(response);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Result result = new Result(ResultCode.PARAM_IS_INVALID);
            result.setData(Collections.EMPTY_LIST);
            try {
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {

        Integer rememberMe = (Integer) request.getAttribute("remember_me");
        JwtUser user = (JwtUser) authResult.getPrincipal();

        // 更新用户封禁状态
        if (user.isAccountNonLocked() && user.getIsLock().equals(UserStatus.LOCK.getStatus())){
            User userById = userService.getUserById(user.getId());
            userById.setIsLock(UserStatus.UN_LOCK.getStatus());
            userService.updateUser(userById);
        }

        logger.info("jwtUser : {}", user.toString());
        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
        String token;
        setResponse(response);
        if (rememberMe.equals(REMEMBER)) {
            token = jwtTokenUtils.createToken(user, true);
        } else {
            token = jwtTokenUtils.createToken(user, false);
        }
        response.setHeader(TOKEN_HEADER_AUTHORIZATION, TOKEN_PREFIX + token);
        // 存储到redis中
        redisTokenService.saveToken(token);

        User user1 = userService.getUserByNick(user.getUsername());

        // 看看有没有被强制登出的用户
        if (redisTokenService.isUserLock(user.getId())){
            redisTokenService.deleteUserLock(user.getId());
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", user1.getUserId());
        userMap.put("face", user1.getFace());
        userMap.put("role", user1.getRole());
        userMap.put("nick",user1.getNick());

        ObjectMapper objectMapper = new ObjectMapper();
        Result<Map> result = new Result<>();
        result.setResultCode(ResultCode.SUCCESS);
        Map<String, Object> map = new HashMap<>();
        map.put("token", TOKEN_PREFIX + token);
        map.put("user", userMap);
        result.setData(map);

        // 记录用户登陆日志
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setUserId(user1.getUserId());
        loginRecord.setIp(GeneralUtils.getIpAddress(request));
        loginRecord.setUserAgent(request.getHeader("user-agent") == null ? "" : request.getHeader("user-agent"));
        loginRecordService.insertLoginRecord(loginRecord);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        setResponse(response);
        Result result = new Result(ResultCode.USER_LOGIN_ERROR);
        result.setMsg(failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }

    private void setResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
