package cc.dmji.api.security;

import cc.dmji.api.common.Result;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.User;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static cc.dmji.api.constants.SecurityConstants.*;

/**
 * Created by echisan on 2018/5/18
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    @Autowired
    private RedisTokenService redisTokenService;

    @Autowired
    private UserService userService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String h = request.getHeader("Authorization");

        if (h == null || !h.startsWith(TOKEN_PREFIX)) {
            response.setHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER, String.valueOf(ResultCode.USER_NOT_LOGINED.getCode()));
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request, response);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TOKEN_HEADER_AUTHORIZATION);
        if (token != null) {
            String realToken = token.replace(TOKEN_PREFIX, "");

            // 检查redis是否存在该token，如果没有返回null
            if (!redisTokenService.hasToken(realToken)) {
                logger.debug("用户："+new JwtTokenUtils().getUsername(realToken));
                logger.info("redis中找不到该token:[{}]，该token已过时或不合法", realToken);
                response.setHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER, String.valueOf(ResultCode.USER_EXPIRATION.getCode()));
                return null;
            }

            JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
            try {
                if (jwtTokenUtils.validateToken(realToken)) {
                    JwtTokenUtils.Payload payload = jwtTokenUtils.getPayload(realToken);
                    logger.debug("username is {}", payload.getUsername());

                    // 查看该用户是不是已被锁定
                    if (redisTokenService.isUserLock(payload.getUid())){
                        // 强制登出
                         redisTokenService.invalidToken(realToken);
                        // 删除该记录
                         redisTokenService.deleteUserLock(payload.getUid());
                        response.setHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER, String.valueOf(ResultCode.USER_ACCOUNT_FORBIDDEN.getCode()));
                        User user = userService.getUserById(payload.getUid());
                        request.setAttribute("ts",user.getLockTime().getTime());
                        return null;
                    }
                    return new UsernamePasswordAuthenticationToken(
                            payload.getUsername(),
                            null,
                            Collections.singleton(
                                    new SimpleGrantedAuthority(payload.getRole())
                            )
                    );
                }
            } catch (ExpiredJwtException e) {
                logger.info("该token已经过时");
                // 若该token已过期则从redis中删除
                redisTokenService.invalidToken(realToken);
                logger.info("并从redis中删除");
                response.setHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER, String.valueOf(ResultCode.USER_EXPIRATION.getCode()));
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("其他错误,也把token从redis中清除");
                redisTokenService.invalidToken(realToken);
                response.setHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER, String.valueOf(ResultCode.PERMISSION_DENY.getCode()));
            }
            return null;
        }
        response.setHeader(SecurityConstants.TOKEN_RESULT_CODE_HEADER, String.valueOf(ResultCode.USER_NOT_LOGINED.getCode()));
        return null;
    }

}
