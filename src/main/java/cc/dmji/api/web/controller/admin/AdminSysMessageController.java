package cc.dmji.api.web.controller.admin;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.User;
import cc.dmji.api.entity.v2.MessageV2;
import cc.dmji.api.entity.v2.SysMessage;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.SysMsgTargetType;
import cc.dmji.api.service.SysMessageService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.service.v2.MessageV2Service;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.JwtUserInfo;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/admin/messages")
public class AdminSysMessageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminSysMessageController.class);

    @Autowired
    private SysMessageService sysMessageService;
    @Autowired
    private MessageV2Service messageV2Service;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    @PostMapping
    @UserLog("发送系统消息")
    public Result postSysMessage(@RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);
        String title = requestMap.get("title");
        String content = requestMap.get("content");
        String targetTypeString = requestMap.get("type");
        if (!StringUtils.hasText(title)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "标题不能为空");
        }
        if (!StringUtils.hasText(content)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "内容不能为空");
        }
        if (!StringUtils.hasText(targetTypeString) || !DmjiUtils.isPositiveNumber(targetTypeString)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "类型错误");
        }
        SysMsgTargetType sysMsgTargetType;
        if ((sysMsgTargetType = SysMsgTargetType.byCode(Integer.valueOf(targetTypeString))) == null) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "不存在的类型");
        }

        SysMessage sysMessage = new SysMessage();
        sysMessage.setTitle(title);
        sysMessage.setStatus(Status.NORMAL);
        sysMessage.setPublisherUid(jwtUserInfo.getUid());
        sysMessage.setSysMsgTargetType(sysMsgTargetType.getCode());
        sysMessage.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sysMessage.setContent(content);
        SysMessage insert = sysMessageService.insert(sysMessage);
        logger.debug("插入的新系统消息的标题:{}，对象类型:{}", insert.getTitle(), insert.getSysMsgTargetType());

        /*------------根据对象清除缓存--------------*/
        cleanUsersMessagesCache(sysMsgTargetType);
        /*--------------------------*/
        return getSuccessResult(sysMessage);
    }

    @DeleteMapping("{smId}")
    @UserLog("删除指定id的系统消息")
    public Result deleteSystemMessage(@PathVariable("smId") Long smid) {
        SysMessage sysMessage = sysMessageService.getById(smid);
        if (sysMessage == null) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "该系统消息不存在，无法删除");
        }
        sysMessage.setStatus(Status.DELETE);
        SysMessage update = sysMessageService.update(sysMessage);
        return getSuccessResult(update);
    }

    @PostMapping("/users")
    @SuppressWarnings("unchecked")
    public Result publishSysMessageToUsers(@RequestBody Map<String, Object> requestMap,
                                           HttpServletRequest request) {
        List<Integer> userIds = (List<Integer>) requestMap.get("ids");
        String title = (String) requestMap.get("title");
        String content = (String) requestMap.get("content");

        if (userIds == null || userIds.size() == 0) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "用户id不能为空");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "标题或内容不能为空");
        }
        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);
        List<MessageV2> messageV2List = new ArrayList<>();

        userIds.forEach(id -> {
            MessageV2 messageV2 = new MessageV2();
            messageV2.setContent(content);
            messageV2.setRead(false);
            messageV2.setTitle(title);
            messageV2.setType(MessageType.SYSTEM);
            messageV2.setCreateTime(new Timestamp(System.currentTimeMillis()));
            messageV2.setPublisherUid(jwtUserInfo.getUid());
            messageV2.setStatus(Status.NORMAL);
            messageV2.setUid(id.longValue());
            messageV2.setSysMessageId(0L);
            messageV2List.add(messageV2);
        });
        List<MessageV2> insertAll = messageV2Service.insertAll(messageV2List);

        List<Long> ids = new ArrayList<>();
        insertAll.forEach(messageV2 -> ids.add(messageV2.getUid()));
        /*清除部分用户的消息统计缓存*/
        cleanUsersMessageCache(ids);
        /*-------------------------*/
        List<Long> msgIds = new ArrayList<>();
        insertAll.forEach(messageV2 -> msgIds.add(messageV2.getId()));
        return getSuccessResult(msgIds);
    }

    @DeleteMapping("/users/messages")
    @SuppressWarnings("unchecked")
    @UserLog("批量删除用户的消息")
    public Result deleteUserMessage(@RequestBody Map<String,Object> requestMap){
        List<Integer> midsInt = (List<Integer>) requestMap.get("mids");
        List<Long> mids = new ArrayList<>();
        midsInt.forEach(id -> mids.add(id.longValue()));
        List<MessageV2> messageV2List = messageV2Service.listIdIn(mids);
        if (messageV2List!=null && messageV2List.size()!=0){
            messageV2List.forEach(messageV2 -> messageV2.setStatus(Status.DELETE));
            List<MessageV2> messageV2List1 = messageV2Service.insertAll(messageV2List);
            return getSuccessResult(messageV2List1);
        }
        return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"删除失败，找不到需要被删除的消息");
    }


    @GetMapping
    @UserLog("获取系统消息列表")
    public Result listSysMessage(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                 @RequestParam(value = "ps", required = false, defaultValue = "50") Integer ps) {

        Page<SysMessage> sysMessages = sysMessageService.listSysMessages(pn, ps);
        PageInfo pageInfo = new PageInfo(pn, ps, sysMessages.getTotalElements());
        List<SysMessage> sysMessageList = sysMessages.getContent();
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("page", pageInfo);
        resultMap.put("messages", sysMessageList);
        return getSuccessResult(resultMap);
    }

    /**
     * 清空所有用户的消息缓存，因为发了系统消息需要更新
     */
    private void cleanAllUserMessageCache() {
        RedisConnectionFactory connectionFactory = stringRedisTemplate.getConnectionFactory();
        if (connectionFactory != null) {
            ScanOptions scanOptions = ScanOptions.scanOptions().match(RedisKey.USER_MSG_COUNT_CACHE + "*").build();
            RedisConnection connection = connectionFactory.getConnection();

            Cursor<byte[]> scan = connection.scan(scanOptions);
            Set<String> keySet = new HashSet<>();
            while (scan.hasNext()) {
                keySet.add(new String(scan.next()));
            }
            Long delete = stringRedisTemplate.delete(keySet);
            logger.debug("删除了用户信息缓存{}条", delete);
        }
    }

    /**
     * 删除部分用户的消息缓存
     *
     * @param ids 需要删除的用户id列表
     */
    @Async
    void cleanUsersMessageCache(List<Long> ids) {
        Set<String> keys = new HashSet<>();
        ids.forEach(id -> keys.add(RedisKey.USER_MSG_COUNT_CACHE + id));
        Long delete = stringRedisTemplate.delete(keys);
        logger.debug("删除了用户信息缓存{}条", delete);
    }


    @Async
    void cleanUsersMessagesCache(SysMsgTargetType sysMsgTargetType){
        switch (sysMsgTargetType){
            case ADMIN:{
                Long delete = stringRedisTemplate.delete(getUserCacheKeyListByRole(Role.ADMIN));
                logger.debug("删除了系统管理员的信息缓存{}条", delete);
            }
            case MANAGER:{
                Long delete = stringRedisTemplate.delete(getUserCacheKeyListByRole(Role.MANAGER));
                logger.debug("删除了管理员的信息缓存{}条", delete);
            }
            case ALL:{
                cleanAllUserMessageCache();
            }
            default:{
                logger.debug("default do nothing");
            }
        }
    }

    private List<String> getUserCacheKeyListByRole(Role role){
        List<User> userList = userService.listUserByRole(role);
        List<String> keyList = new ArrayList<>();
        userList.forEach(user -> keyList.add(RedisKey.USER_MSG_COUNT_CACHE+user.getUserId()));
        return keyList;
    }
}
