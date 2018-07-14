package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.entity.Message;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.*;
import cc.dmji.api.service.MessageService;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/6/9
 */
@RestController
@RequestMapping("/admin/users")
public class AdminUserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    // 降为普通用户
    private static final int TO_USER = 0;
    // 提升为管理员
    private static final int TO_MANAGER = 1;
    // 提升为系统管理员
    private static final int TO_ADMIN = 2;

    // 解封用户
    private static final int UNLOCK_USER = 0;
    // 锁定用户
    private static final int LOCK_USER = 1;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisTokenService redisTokenService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<Result> listUser(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                           @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps) {

        List<User> userList = userService.listUser(pn, ps);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(ps);
        pageInfo.setPageNumber(pn);
        pageInfo.setTotalSize(userService.countUsers());
        Map<String, Object> data = new HashMap<>();
        data.put("page", pageInfo);
        data.put("users", userList);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Result> deleteUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
        }
        userService.deleteUserById(userId);
        return getResponseEntity(HttpStatus.OK, getSuccessResult("删除成功"));
    }

    @GetMapping("/online")
    public ResponseEntity<Result> getOnlineUser() {
        return getResponseEntity(HttpStatus.OK, getSuccessResult());
    }

    @GetMapping("/{uid}")
    public ResponseEntity<Result> getUser(@PathVariable("uid") String uid) {
        User user = userService.getUserById(uid);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(user));
    }

    @GetMapping("/nick")
    public ResponseEntity<Result> listUsersLike(@RequestParam("nick") String nick,
                                                @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps) {

        if (StringUtils.isEmpty(nick)) {
            return listUser(pn, ps);
        }

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(ps);
        pageInfo.setPageNumber(pn);
        pageInfo.setTotalSize(userService.countUsersNickLike(nick));
        List<User> userList = userService.listUsersNickLike(nick, pn, ps);
        Map<String, Object> data = new HashMap<>();
        data.put("page", pageInfo);
        data.put("users", userList);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }


    @PutMapping("/{uid}/role/{action}")
    public ResponseEntity<Result> updateUserRole(@PathVariable("uid") String uid,
                                                 @PathVariable("action") Integer action,
                                                 @RequestBody(required = false) Map<String, String> requestMap,
                                                 HttpServletRequest request) {

        if (requestMap == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "密码不能为空"));
        }
        // 每次进行提升权限都需要进行输入管理员的密码进行确认
        // 管理员密码
        String pwd = requestMap.get("pwd");
        if (StringUtils.isEmpty(pwd)) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "密码不能为空"));
        }
        // 当前用户即管理员的用户信息
        User currentUser = getCurrentUser(request);
        User user = userService.getUserById(uid);

        if (user == null || action == null) {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
        }

        // 如果密码不一致则403
        if (!bCryptPasswordEncoder.matches(pwd,currentUser.getPwd())) {
            return getResponseEntity(HttpStatus.FORBIDDEN,
                    getErrorResult(ResultCode.PERMISSION_DENY, "密码不正确，无权限对用户[" + user.getNick() + "]进行操作"));
        }

        String currentRole = currentUser.getRole();
        String targetRole = user.getRole();

        // 消息通知
        Message msg = new Message();
        msg.setTitle("系统通知");
        msg.setmStatus(Status.NORMAL.name());
        msg.setPublisherUserId(currentUser.getUserId());
        msg.setIsRead(MessageConstants.NOT_READ);
        msg.setType(MessageType.SYSTEM.name());
        msg.setUserId(user.getUserId());

        String message;
        switch (action) {
            case TO_USER:
                // 如果当前用户是[管理员] 目标用户是[管理员]或[系统管理员]
                // 则抛出403
                if (isCurrMangerTarMangerOrAdmin(currentRole, targetRole)) {
                    return getResponseEntity(HttpStatus.FORBIDDEN,
                            getErrorResult(ResultCode.PERMISSION_DENY, "无权限对管理员[" + user.getNick() + "]进行操作"));
                }
                user.setRole(Role.USER.getName());
                message = "已将该用户[" + user.getNick() + "]设置为[普通用户]";
                msg.setContent("你的管理员权限已被撤回，更多详情请联系help@darker.online。");
                break;
            case TO_MANAGER:
                if (isCurrMangerTarMangerOrAdmin(currentRole, targetRole)) {
                    return getResponseEntity(HttpStatus.FORBIDDEN,
                            getErrorResult(ResultCode.PERMISSION_DENY, "无权限对管理员[" + user.getNick() + "]进行操作"));
                }
                user.setRole(Role.MANAGER.getName());
                message = "已将该用户[" + user.getNick() + "]设置为[管理员]";
                msg.setContent("恭喜你成为我们Darker的管理员啦！");
                break;
            case TO_ADMIN:
                if (isCurrMangerTarMangerOrAdmin(currentRole, targetRole)) {
                    return getResponseEntity(HttpStatus.FORBIDDEN,
                            getErrorResult(ResultCode.PERMISSION_DENY, "无权限对管理员[" + user.getNick() + "]进行操作"));
                }
                user.setRole(Role.ADMIN.getName());
                message = "已将该用户[" + user.getNick() + "]设置为[系统管理员]";
                msg.setContent("哇塞！恭喜你成为Darker的系统管理员啦！lalala~");
                break;
            default:
                return getResponseEntity(HttpStatus.BAD_REQUEST,
                        getErrorResult(ResultCode.PARAM_IS_INVALID, "不能识别参数[ " + action + " ]"));
        }

        User updateUser = userService.updateUser(user);
        sendMessage(msg);
        redisTokenService.addUserLock(user.getUserId());

        // TODO 消息通知该用户，已被设置为管理员或者降级为普通用户
        return getResponseEntity(HttpStatus.OK, getSuccessResult(updateUser, message));
    }

    public boolean isCurrMangerTarMangerOrAdmin(String currentUserRole, String targetUserRole) {
        // 如果当前用户是[管理员] 目标用户是[管理员]或[系统管理员]
        // 则抛出403
        if (currentUserRole.equals(Role.MANAGER.getName())) {
            return targetUserRole.equals(Role.MANAGER.getName()) ||
                    targetUserRole.equals(Role.ADMIN.getName());
        }
        return false;
    }

    @PutMapping(value = {"/{uid}/lock/{action}/{time}", "/{uid}/lock/{action}"})
    public ResponseEntity<Result> lockUser(@PathVariable("uid") String uid,
                                           @PathVariable("action") Integer action,
                                           @PathVariable(value = "time", required = false) Integer time,
                                           HttpServletRequest request) {

        User user = userService.getUserById(uid);
        if (user == null || action == null) {
            return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
        }

        // 普通管理员只能用于封禁普通用户，不能封禁系统管理员，若是有此举动直接403警告这狗崽子
        User currentUser = getCurrentUser(request);
        if (currentUser.getRole().equals(Role.MANAGER.getName())
                && user.getRole().equals(Role.ADMIN.getName())) {
            return getResponseEntity(HttpStatus.FORBIDDEN,
                    getErrorResult(ResultCode.PERMISSION_DENY, "权限不足，对系统管理员[" + user.getNick() + "]进行封禁"));
        }

        String message;
        switch (action) {
            case LOCK_USER:
                // time 以秒为单位
                if (time == 0) {
                    return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.PARAM_IS_INVALID, "封禁时间不能为0"));
                }
                user.setIsLock(UserStatus.LOCK.getStatus());
                user.setLockTime(time);
                message = "已封禁账号[" + user.getNick() + "],锁定时长为[" + time + "]";
                break;
            case UNLOCK_USER:
                user.setIsLock(UserStatus.UN_LOCK.getStatus());
                // 若解封则将时间设置为0
                user.setLockTime(0);
                message = "已解封账号[" + user.getNick() + "]";
                break;
            default:
                return getResponseEntity(HttpStatus.BAD_REQUEST,
                        getErrorResult(ResultCode.PARAM_IS_INVALID, "不能识别参数[ " + action + " ]"));
        }

        // TODO 消息通知该用户, 已被某管理员封禁/解封

        // 若是被封禁则记录该用户到redis中
        redisTokenService.addUserLock(user.getUserId());

        User updateUser = userService.updateUser(user);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(updateUser, message));
    }

    @PutMapping
    public ResponseEntity<Result> updateUser(@RequestBody User user) {

        User dbUser = userService.getUserById(user.getUserId());

        if (!dbUser.getPwd().equals(user.getPwd())) {
            user.setPwd(bCryptPasswordEncoder.encode(user.getPwd()));
        }

        User updateUser = userService.updateUser(user);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(updateUser, "更新成功"));

    }


    /**
     * 获取当前用户
     *
     * @param request request
     * @return 当前用户
     */
    private User getCurrentUser(HttpServletRequest request) {
        String uid = getUidFromToken(request);
        return userService.getUserById(uid);
    }

    @Async
    public void sendMessage(Message message){
        Message message1 = messageService.insertMessage(message);
        logger.debug("发送的系统通知:{}",message1);
    }


}
