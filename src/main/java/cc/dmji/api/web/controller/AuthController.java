package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.UserStatus;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private JwtTokenUtils jwtTokenUtils;

    @PostMapping("/register")
    public ResponseEntity<Result> createAuthenticationToken(
            @RequestBody User user) {

        String nick = user.getNick();
        String password = user.getPwd();
        String email = user.getEmail();

        if (!DmjiUtils.validUsername(nick)) {
            return new ResponseEntity<>(
                    getErrorResult(ResultCode.PARAM_IS_INVALID, "账号格式不符合要求")
                    ,HttpStatus.BAD_REQUEST);
        }

        if (!DmjiUtils.validPassword(password)) {
            return new ResponseEntity<>(
                    getErrorResult(ResultCode.PARAM_IS_INVALID, "密码格式不符合要求"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!DmjiUtils.validEmail(email)) {
            return new ResponseEntity<>(
                    getErrorResult(ResultCode.PARAM_IS_INVALID, "邮件格式不正确"),
                    HttpStatus.BAD_REQUEST
            );
        }

        // 验证通过
        User newUser = new User();
        newUser.setNick(nick);
        newUser.setEmail(email);
        newUser.setEmailVerified(UserStatus.EMAIL_UN_VERIFY.getStatus());
        newUser.setPwd(password);
        newUser.setRole(Role.USER.getName());
        newUser.setIsLock(UserStatus.UN_LOCK.getStatus());

        try {
            User user1 = userService.insertUser(user);
            user1.setPwd("不让看");
            return new ResponseEntity<Result>(
                    getSuccessResult(user1,"注册成功"),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.info("数据库出现偏差，原因：{}",e.getMessage());
            return new ResponseEntity<>(
                    getErrorResult(ResultCode.SYSTEM_INTERNAL_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Result> logout(@RequestParam("token")String token){
        return getResponseEntity(HttpStatus.OK,getSuccessResult());
    }
}
