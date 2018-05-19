package cc.dmji.api.security;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.web.model.AuthUser;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

import static cc.dmji.api.constants.SecurityConstants.*;

/**
 * Created by echisan on 2018/5/18
 */

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Integer REMEMBER = 1;
    private static final Integer UN_REMEMBER = 0;

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void setUsernameParameter(String usernameParameter) {
        super.setUsernameParameter("principal");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            AuthUser user = new ObjectMapper()
                    .readValue(request.getInputStream(), AuthUser.class);
            request.setAttribute("remember_me",user.getRemember_me());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getPrincipal(),
                            user.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        Integer rememberMe = (Integer) request.getAttribute("remember_me");
        JwtUser user = (JwtUser) authResult.getPrincipal();
        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
        String token;
        if (rememberMe.equals(REMEMBER)){
            token = jwtTokenUtils.createToken(user,true);
        }else {
            token = jwtTokenUtils.createToken(user, false);
        }
        response.setHeader(TOKEN_HEADER_AUTHORIZATION, TOKEN_PREFIX + token);

        setResponse(response);

        ObjectMapper objectMapper = new ObjectMapper();
        Result<Map> result = new Result<>();
        result.setResultCode(ResultCode.SUCCESS);
        Map<String, String> map = new HashMap<>();
        map.put("token", TOKEN_PREFIX + token);
        result.setData(map);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        setResponse(response);
        Result result = new Result(ResultCode.USER_LOGIN_ERROR);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }

    private void setResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
