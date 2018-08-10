package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;

import static cc.dmji.api.constants.SecurityConstants.TOKEN_HEADER_AUTHORIZATION;
import static cc.dmji.api.constants.SecurityConstants.TOKEN_PREFIX;

/**
 * Created by echisan on 2018/5/16
 */
@RestController
@RequestMapping("/tokens")
public class TokenController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private RedisTokenService redisTokenService;

    @GetMapping
    public Result checkLoginStatus(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TOKEN_HEADER_AUTHORIZATION);
        if (token != null) {
            String realToken = token.replace(TOKEN_PREFIX, "");

            // 检查redis是否存在该token，如果没有返回null
            if (!redisTokenService.hasToken(realToken)) {
                logger.info("redis中找不到该token:[{}]，该token已过时或不合法", realToken);
                return getErrorResult(ResultCode.USER_EXPIRATION);
            }
            JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
            try {
                if (jwtTokenUtils.validateToken(realToken)) {
                    JwtTokenUtils.Payload payload = jwtTokenUtils.getPayload(realToken);
                    logger.debug("username is {}", payload.getUsername());
                    return getSuccessResult("ojbk");
                }
            } catch (ExpiredJwtException e) {
                logger.info("该token已经过时");
                // 若该token已过期则从redis中删除
                getErrorResult(ResultCode.USER_EXPIRATION);
            } catch (Exception e) {
                logger.info("其他错误");
                getErrorResult(ResultCode.USER_EXPIRATION);
            }
        }
        return getErrorResult(ResultCode.USER_EXPIRATION);
    }
}
