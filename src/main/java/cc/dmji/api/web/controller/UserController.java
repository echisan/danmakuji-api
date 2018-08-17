package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.Sex;
import cc.dmji.api.enums.UserStatus;
import cc.dmji.api.service.MailService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by echisan on 2018/5/16
 */
@RestController
@RequestMapping(value = "/users")
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
    @UserLog("账号注册")
    public ResponseEntity<Result> registerUser(@RequestBody User user) throws MessagingException {
        String nick = user.getNick();
        String password = user.getPwd();
        String email = user.getEmail();

        if (!DmjiUtils.validUsername(nick)) {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "账号格式不符合要求"));
        } else {
            User dbUser = userService.getUserByNick(nick);
            if (dbUser != null) {
                return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "该用户已存在"));
            }
        }
        if (!DmjiUtils.validPassword(password)) {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "密码格式不符合要求"));
        }

        if (!DmjiUtils.validEmail(email)) {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "邮箱格式不正确"));
        } else {
            User dbUser = userService.getUserByEmail(email);
            if (dbUser != null) {
                return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "该邮箱地址已被使用"));
            }
        }

        // 验证通过
        User newUser = new User();
        newUser.setNick(nick);
        newUser.setEmail(email);
        newUser.setEmailVerified(UserStatus.EMAIL_UN_VERIFY.getStatus());
        newUser.setPwd(password);
        newUser.setRole(Role.USER.getName());
        newUser.setAge(0);
        newUser.setSex(Sex.OTHER.getValue());
        newUser.setPhone("");
        newUser.setPhoneVerified(UserStatus.PHONE_UN_VERIFY.getStatus());
        newUser.setFace("");
        newUser.setIsLock(UserStatus.UN_LOCK.getStatus());
        newUser.setLockTime(null);


        try {
            User user1 = userService.insertUser(newUser);
            user1.setPwd("不让看");
            logger.debug("异步发送验证邮件");
            String key = RedisKey.VERIFY_EMAIL_KEY + user1.getUserId();
            String uuid = GeneralUtils.getUUID();
            stringRedisTemplate.opsForValue().set(key, uuid, 20L, TimeUnit.MINUTES);
            logger.debug("发送验证的邮件，key:{},uuid:{}", key, uuid);

            mailService.sendVerifyEmail(user1.getEmail(), user1.getUserId(), uuid);

            return new ResponseEntity<Result>(
                    getSuccessResult(user1, "注册成功"),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.info("数据库出现偏差，原因：{}", e.getMessage());
            return new ResponseEntity<>(
                    getErrorResult(ResultCode.SYSTEM_INTERNAL_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @GetMapping("/{userId}")
    @UserLog("获取单个用户信息")
    public ResponseEntity getUser(@PathVariable Long userId, HttpServletRequest request) {
        User user = userService.getUserById(userId);
        Long currentUserId = getUidFromRequest(request);

        if (user != null) {
            // 如果不是本人的话只能获取一些奇怪的东西了
            if (currentUserId == null || !currentUserId.equals(user.getUserId())) {
//                UserInfo userInfo = new UserInfo();
//                userInfo.setSex(user.getSex());
//                userInfo.setFace(user.getFace());
//                userInfo.setUid(user.getUserId());
//                userInfo.setNick(user.getNick());
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("sex", user.getSex());
                userInfo.put("face", user.getFace());
                userInfo.put("uid", String.valueOf(user.getUserId()));
                userInfo.put("nick", user.getNick());
                userInfo.put("sign", user.getSign());
                return getResponseEntity(HttpStatus.OK, getSuccessResult(userInfo));
            }
            user.setPwd("");
            return getResponseEntity(HttpStatus.OK, getSuccessResult(user));
        }
        return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
    }

    //    @DeleteMapping("/{userId}")
    @UserLog("注销账号")
    public ResponseEntity<Result> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        Long uid = getUidFromRequest(request);
        User user = userService.getUserById(userId);
        if (user == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
        }
        if (!user.getUserId().equals(uid)) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY));
        }
        userService.deleteUserById(userId);
        return getResponseEntity(HttpStatus.OK, getSuccessResult());
    }

    @PutMapping("/{userId}/pwd")
    public ResponseEntity<Result> updatePassword(@PathVariable Long userId,
                                                 @RequestBody Map<String, String> requestMap,
                                                 HttpServletRequest request) {
        // ----- 修改密码 -----
        Long uid = getUidFromRequest(request);
        User dbUser = userService.getUserById(userId);
        if (!dbUser.getUserId().equals(uid)) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY));
        }
        String oldPassword = requestMap.get("opwd");
        String newPassword = requestMap.get("cpwd");

        if (!bCryptPasswordEncoder.matches(oldPassword, dbUser.getPwd())) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "原密码错误，无法修改");
        }
        if (oldPassword.equals(newPassword)) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.DATA_IS_WRONG, "新密码不能与旧密码相同");
        }
        if (StringUtils.hasText(newPassword) && DmjiUtils.validPassword(newPassword)) {
            dbUser.setPwd(bCryptPasswordEncoder.encode(newPassword));
        } else {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.DATA_IS_WRONG, "密码格式有误");
        }
        User updateUser = userService.updateUser(dbUser);
        updateUser.setPwd("");
        return getSuccessResponseEntity(getSuccessResult(updateUser));
    }

    @PutMapping("/{userId}")
    @UserLog("更新用户信息")
    public ResponseEntity<Result> updateUser(@PathVariable Long userId, @RequestBody User user,
                                             HttpServletRequest request) throws MessagingException {

        // 这里只允许修改的参数有：昵称，年龄，头像，个性签名，性别

        logger.info("修改用户信息[PUT] user:[{}]", user.toString());
        Long uid = getUidFromRequest(request);
        User dbUser = userService.getUserById(userId);
        if (!dbUser.getUserId().equals(uid)) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY));
        }


        // -----------------

        // 修改昵称
        if (!StringUtils.isEmpty(user.getNick())) {
            if (!DmjiUtils.validUsername(user.getNick())) {
                return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "昵称格式不正确"));
            }
            if (userService.getUserByNick(user.getNick()) != null) {
                return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "该昵称已被使用"));
            }
            dbUser.setNick(user.getNick());
        }

        // 修改电话号码
        if (!StringUtils.isEmpty(user.getPhone())) {
//            dbUser.setPhone(user.getPhone());
//            dbUser.setPhoneVerified(UserStatus.PHONE_UN_VERIFY.getStatus());
        }
        // 修改年龄
        if (!StringUtils.isEmpty(user.getAge())) {
            dbUser.setAge(user.getAge());
        }

        // 修改头像
        if (!StringUtils.isEmpty(user.getFace())) {
            dbUser.setFace(user.getFace());
        }

        // 修改个性前面
        if (!StringUtils.isEmpty(user.getSign())) {
            dbUser.setSign(user.getSign());
        }

        // 修改性别
        if (!StringUtils.isEmpty(user.getSex())) {
            String sex = user.getSex();
            Sex[] values = Sex.values();
            for (Sex s : values) {
                if (s.getValue().equals(sex)) {
                    dbUser.setSex(s.getValue());
                }
            }
        }

        User updatedUser = userService.updateUser(dbUser);
        updatedUser.setPwd("");
        logger.debug("更新后的用户数据{}", updatedUser.toString());
        return getSuccessResponseEntity(getSuccessResult(updatedUser, "修改成功"));
    }

    @PutMapping("/{uid}/email")
    @UserLog("更换邮箱")
    public Result updateUserEmail(@PathVariable("uid") Long uid,
                                  @RequestBody Map<String, String> requestMap,
                                  HttpServletRequest request) {

        if (!uid.equals(getUidFromRequest(request))) {
            return getErrorResult(ResultCode.PERMISSION_DENY);
        }

        String email = requestMap.get("email");
        String rcode = requestMap.get("rcode");
        if (!StringUtils.hasText(email)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "邮箱地址不能为空");
        }
        User userByEmail = userService.getUserByEmail(email);
        if (userByEmail != null) {
            return getErrorResult(ResultCode.DATA_ALREADY_EXIST, "该邮件地址已被使用");
        }

        if (!DmjiUtils.validEmail(email)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "邮箱地址格式不正确，请确保是正确的邮箱地址");
        }

        User user = userService.getUserById(uid);

        // 如果该邮箱已经验证过了，则需要发邮箱去验证
        if (user.getEmailVerified().equals(UserStatus.EMAIL_VERIFY.getStatus())) {
            if (!StringUtils.hasText(rcode)) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "验证码不能为空");
            }
            String s = stringRedisTemplate.opsForValue().get(RedisKey.RESET_EMAIL_VERIFY_CODE + uid);
            if (s == null) {
                return getErrorResult(ResultCode.DATA_EXPIRATION, "验证码已过期，请重新获取");
            }
            // 如果验证码不正确
            if (!rcode.equals(s)) {
                return getErrorResult(ResultCode.DATA_IS_WRONG, "验证码不正确");
            }

        }
        // 如果邮箱地址未验证的话，允许直接修改 || 验证码正确，则修改验证码，并发送验证邮件
        user.setEmail(email);
        user.setEmailVerified(UserStatus.EMAIL_UN_VERIFY.getStatus());
        User updateUser = userService.updateUser(user);

        try {
            String uuid = GeneralUtils.getUUID();
            stringRedisTemplate.boundValueOps(RedisKey.VERIFY_EMAIL_KEY + uid).set(uuid, 20L, TimeUnit.MINUTES);
            mailService.sendVerifyEmail(updateUser.getEmail(), updateUser.getUserId(), uuid);
        } catch (MessagingException e) {
            e.printStackTrace();
            return getErrorResult(ResultCode.SYSTEM_INTERNAL_ERROR, "服务器繁忙，请稍后再试");
        }
        return getSuccessResult("修改成功，验证邮箱已发往目标dalao的新邮箱，进及时处理");

    }


    @PostMapping("/pwd/forget/cemail")
    @UserLog("忘记密码，确认邮箱")
    public Result checkEmailForgetPwd(@RequestBody Map<String, String> requestMap) throws JsonProcessingException {
        String email = requestMap.get("email");
        if (!StringUtils.hasText(email)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "邮件地址不能为空");
        }
        if (!DmjiUtils.validEmail(email)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "邮件地址格式不合法");
        }
        User userByEmail = userService.getUserByEmail(email);
        if (userByEmail == null) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "账号不存在");
        }
        String formatEmail = userByEmail.getEmail();
        int i = formatEmail.indexOf("@");
        formatEmail = String.valueOf(formatEmail.charAt(0)) + "***" + formatEmail.substring(i);
        Map<String, String> map = new HashMap<>(4);
        map.put("value", formatEmail);

        String uuid = GeneralUtils.getUUID();
        String ticketKey = RedisKey.FORGET_PWD_TICKET + uuid;
        map.put("ticket", uuid);
        // 存入ticket到redis
        stringRedisTemplate.opsForValue().set(ticketKey, new ObjectMapper().writeValueAsString(userByEmail), 2L, TimeUnit.HOURS);
        return getSuccessResult(map);

    }

    @GetMapping("/pwd/forget/vcode")
    @UserLog("忘记密码，请求验证码")
    public Result forgetPwdAndSendVerifyEmail(HttpServletRequest request,
                                              @RequestParam("ticket") String ticket) throws IOException {

        // 需要求60秒钟请求一次的限制
        // 用户信息从ticket获取
        if (!StringUtils.hasText(ticket)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "凭证不能为空");
        }

        BoundValueOperations<String, String> valueOps =
                stringRedisTemplate.boundValueOps(RedisKey.FORGET_PWD_TICKET + ticket);

        String userJson;
        if ((userJson = valueOps.get()) == null) {
            return getErrorResult(ResultCode.USER_TICKET_EXPIRATION, "该凭证已过期，请重新确认账号");
        }
        User user = new ObjectMapper().readValue(userJson, User.class);

        // 生成验证码
        String verifyCode = DmjiUtils.generateVerifyCode();
        String key = RedisKey.FORGET_PWD_VERIFY_CODE + ticket;
        String ip = GeneralUtils.getIpAddress(request);
        String limitKey = RedisKey.FORGET_PWD_VERIFY_LIMIT + ip;
        BoundValueOperations<String, String> limitKeyOperation = stringRedisTemplate.boundValueOps(limitKey);
        if (limitKeyOperation.get() != null) {
            Long expire = limitKeyOperation.getExpire();
            return getErrorResult(ResultCode.USER_REQUEST_FREQUENTLY, "请求频繁，请在" + expire + "秒后再试!");
        }

        logger.debug("忘记密码的请求验证码:{}", verifyCode);
        // 往redis里存入验证码
        stringRedisTemplate.boundValueOps(key).set(verifyCode, 5L, TimeUnit.MINUTES);
        // 往redis里存入请求限制
        stringRedisTemplate.boundValueOps(limitKey).set(ip, 60L, TimeUnit.SECONDS);
        try {
            String title = "【Darker】账号安全中心-找回登录密码";
            String content = "尊敬的用户，你好:<br/>" +
                    "  您正在进行找回登录密码的重置操作，本次请求的邮件验证码是：<strong>" + verifyCode + "</strong>" +
                    "(为了保证你的账号的安全性，请在5分钟内完成设置)。" +
                    "本验证码5分钟内有效，请及时输入。<br/>" +
                    "  为了保证账号安全，请勿泄露此验证码。<br/>" +
                    "  祝您生活愉快!<br/><br/>" +
                    "  (这是一封自动发送的邮件，请不要直接回复，若有疑问请联系help@darker.online)";
            mailService.sendEmail(user.getEmail(), title, content);
        } catch (MessagingException e) {
            e.printStackTrace();
            return getErrorResult(ResultCode.SYSTEM_INTERNAL_ERROR, "服务器正忙，请稍后再试。");
        }

        return getSuccessResult("验证码已发送，请前往目标邮箱查看。");
    }

    @PutMapping("/pwd/forget/reset")
    @UserLog("忘记密码后的更换密码")
    public Result resetPwdAfterForgetPwd(@RequestBody Map<String, String> requestMap) throws IOException {
        String pwd = requestMap.get("pwd");
        String rcode = requestMap.get("rcode");
        String ticket = requestMap.get("ticket");

        if (!StringUtils.hasText(ticket)) {
            return getErrorResult(ResultCode.USER_TICKET_INVALID);
        }

        BoundValueOperations<String, String> valueOps =
                stringRedisTemplate.boundValueOps(RedisKey.FORGET_PWD_TICKET + ticket);
        String userJson;
        if ((userJson = valueOps.get()) == null) {
            return getErrorResult(ResultCode.USER_TICKET_EXPIRATION, "该凭证已过期，请重新确认账号");
        }
        User user = new ObjectMapper().readValue(userJson, User.class);

        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps(RedisKey.FORGET_PWD_VERIFY_CODE + ticket);
        String code;
        if ((code = ops.get()) == null) {
            return getErrorResult(ResultCode.DATA_EXPIRATION, "该验证码已过期，请重新获取");
        }
        if (!rcode.equals(code)) {
            return getErrorResult(ResultCode.DATA_IS_WRONG, "验证码有误，请重新输入");
        }

        // 将验证码删了
        stringRedisTemplate.delete(RedisKey.FORGET_PWD_VERIFY_CODE + user.getEmail());
        // ticket也删了
        stringRedisTemplate.delete(RedisKey.FORGET_PWD_TICKET + ticket);

        user.setPwd(bCryptPasswordEncoder.encode(pwd));
        userService.updateUser(user);

        return getSuccessResult("密码修改成功");
    }

}
