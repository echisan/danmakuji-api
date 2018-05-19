package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.UserStatus;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.web.model.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by echisan on 2018/5/16
 */
@RestController
@RequestMapping(value = "users")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册用户时候使用的
     *
     * @return 响应信息
     */
    @PostMapping
    public ResponseEntity<Result> registerUser(@RequestBody User registerUser) {
        if (!DmjiUtils.validUsername(registerUser.getNick())) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID,"用户名格式不正确"));
        }
        if (!DmjiUtils.validPassword(registerUser.getPwd())){
            return getResponseEntity(HttpStatus.BAD_REQUEST,getErrorResult(ResultCode.PARAM_IS_INVALID,"密码格式不正确"));
        }
        if (!DmjiUtils.validEmail(registerUser.getEmail())){
            return getResponseEntity(HttpStatus.BAD_REQUEST,getErrorResult(ResultCode.PARAM_IS_INVALID,"邮箱格式不正确"));
        }

        registerUser.setRole(Role.USER.getName());
        registerUser.setEmailVerified(UserStatus.EMAIL_UN_VERIFY.getStatus());
        registerUser.setIsLock(UserStatus.UN_LOCK.getStatus());

        User user = userService.insertUser(registerUser);
        user.setPwd("才不让看");
        // todo 邮件发送

        return getResponseEntity(HttpStatus.OK, getSuccessResult(user));
    }

    @GetMapping
    public Result getAllUser() {
        List<User> users = userService.listUser();
        return getSuccessResult(users);
    }

}
