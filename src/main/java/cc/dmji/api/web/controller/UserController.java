package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.ValidUserSelf;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.constants.SecurityConstants;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.UserStatus;
import cc.dmji.api.service.MailService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.web.model.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by echisan on 2018/5/16
 */
@RestController
@RequestMapping(value = "users")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final Long VERIFY_UUID_EXPIRATION = 1800L; // 1800L

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 注册用户时候使用的
     *
     * @return 响应信息
     */
    @PostMapping
    public ResponseEntity<Result> registerUser(@RequestBody User registerUser) throws MessagingException {
        if (!DmjiUtils.validUsername(registerUser.getNick())) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "用户名格式不正确"));
        }
        if (!DmjiUtils.validPassword(registerUser.getPwd())) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "密码格式不正确"));
        }
        if (!DmjiUtils.validEmail(registerUser.getEmail())) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "邮箱格式不正确"));
        }

        registerUser.setRole(Role.USER.getName());
        registerUser.setEmailVerified(UserStatus.EMAIL_UN_VERIFY.getStatus());
        registerUser.setIsLock(UserStatus.UN_LOCK.getStatus());
        registerUser.setAge(0);
        registerUser.setPhone("");
        registerUser.setFace("");
        registerUser.setPhoneVerified(UserStatus.PHONE_UN_VERIFY.getStatus());
        registerUser.setLockTime(0);
        User user = userService.insertUser(registerUser);
        user.setPwd("才不让看");
        // 邮件发送
        String uuid = DmjiUtils.getUUID32();
        stringRedisTemplate.opsForValue().set(RedisKey.VERIFY_EMAIL_KEY + user.getUserId(),
                uuid, VERIFY_UUID_EXPIRATION, TimeUnit.SECONDS);
        mailService.sendVerifyEmail(user.getEmail(), user.getUserId(), uuid);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(user));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Result getAllUser(@RequestParam(value = "page", required = false) Integer page,
                             @RequestParam(value = "size", required = false) Integer size) {
        if (page == null && size == null) {
            List<User> users = userService.listUser();
            return getSuccessResult(users);
        } else {
            Page<User> users = userService.listUser(page, size);
            return getSuccessResult(users);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return getResponseEntity(HttpStatus.OK, getSuccessResult(user));
        }
        return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
    }

    @ValidUserSelf
    @DeleteMapping("/{userId}")
    public ResponseEntity<Result> deleteUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
        }
        userService.deleteUserById(userId);
        return getResponseEntity(HttpStatus.OK, getSuccessResult());
    }

    @ValidUserSelf
    @PutMapping("/{userId}")
    public ResponseEntity<Result> updateUser(@PathVariable String userId, @RequestBody User user) throws MessagingException {
        User dbUser = userService.getUserById(userId);
        boolean isEmailChange = false;
        boolean isPhoneChange = false;
        // 修改密码
        if (!StringUtils.isEmpty(user.getPwd())) {
            dbUser.setPwd(bCryptPasswordEncoder.encode(user.getPwd()));
        }
        // 修改邮箱
        if (!StringUtils.isEmpty(user.getFace())) {
            dbUser.setFace(user.getFace());
        }
        // 修改昵称
        if (!StringUtils.isEmpty(user.getNick())) {
            dbUser.setNick(user.getNick());
        }
        // 修改电话号码
        if (!StringUtils.isEmpty(user.getPhone())) {
            dbUser.setPhone(user.getPhone());
            dbUser.setPhoneVerified(UserStatus.PHONE_UN_VERIFY.getStatus());
            isPhoneChange = true;
        }
        // 修改年龄
        if (!StringUtils.isEmpty(user.getAge())) {
            dbUser.setAge(user.getAge());
        }
        // 修改邮箱
        if (!StringUtils.isEmpty(user.getEmail())) {
            dbUser.setEmail(user.getEmail());
            dbUser.setEmailVerified(UserStatus.EMAIL_UN_VERIFY.getStatus());
            isEmailChange = true;
        }

        User updatedUser = userService.updateUser(dbUser);

        // 如果修改了邮箱则验证
        if (isEmailChange) {
            String uuid = DmjiUtils.getUUID32();
            stringRedisTemplate.opsForValue().set(RedisKey.VERIFY_EMAIL_KEY + updatedUser.getUserId(),
                    uuid, VERIFY_UUID_EXPIRATION, TimeUnit.SECONDS);
            mailService.sendVerifyEmail(updatedUser.getEmail(), updatedUser.getUserId(), uuid);
            return getResponseEntity(HttpStatus.OK, getSuccessResult("修改邮箱成功，请前往邮箱进行确认"));
        } else {
            return getResponseEntity(HttpStatus.OK, getSuccessResult("修改成功"));
        }

        // todo 验证手机号码 先不干
    }
}
