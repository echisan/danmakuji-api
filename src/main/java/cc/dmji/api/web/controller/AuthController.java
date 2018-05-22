package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.ValidUserSelf;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.UserStatus;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.DmjiUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by echisan on 2018/5/18
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTokenService redisTokenService;

    @GetMapping("/logout")
    public ResponseEntity<Result> logout(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.TOKEN_HEADER_AUTHORIZATION);
        String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
        if (redisTokenService.hasToken(token)){
            Long tokenIndex = redisTokenService.invalidToken(token);
            logger.info("登出成功, 该tokenIndex为：{}",tokenIndex);
            return getResponseEntity(HttpStatus.OK, getSuccessResult("登出成功"));
        }else {
            return getResponseEntity(HttpStatus.BAD_REQUEST,getErrorResult(ResultCode.PARAM_IS_INVALID));
        }
    }

    @GetMapping("/verify/uid/{uid}/key/{key}")
    public ResponseEntity<Result> verifyEmail(@PathVariable("uid") String uid,
                                              @PathVariable("key") String uuid) {

        String key = RedisKey.VERIFY_EMAIL_KEY + uid;
        String redisUUID = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisUUID)) {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.DATA_EXPIRATION, "该链接已失效"));
        }
        if (uuid.equalsIgnoreCase(redisUUID)) {
            User user = userService.getUserById(uid);
            user.setEmailVerified(UserStatus.EMAIL_VERIFY.getStatus());
            stringRedisTemplate.delete(key);
            return getResponseEntity(HttpStatus.OK, getSuccessResult("邮箱验证成功!"));
        } else {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.DATA_IS_WRONG));
        }
    }
}
