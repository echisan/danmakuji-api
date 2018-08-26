package cc.dmji.api.web.controller.admin;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.v2.MessageV2;
import cc.dmji.api.entity.v2.SysMessage;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.SysMsgTargetType;
import cc.dmji.api.service.SysMessageService;
import cc.dmji.api.service.v2.MessageV2Service;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.JwtUserInfo;
import cc.dmji.api.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/messages")
public class AdminSysMessageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminSysMessageController.class);

    @Autowired
    private SysMessageService sysMessageService;
    @Autowired
    private MessageV2Service messageV2Service;

    @PostMapping
    @UserLog("发送系统消息")
    public Result postSysMessage(@RequestBody Map<String,String> requestMap, HttpServletRequest request){
        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);
        String title = requestMap.get("title");
        String content = requestMap.get("content");
        String targetTypeString = requestMap.get("type");
        if (!StringUtils.hasText(title)){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"标题不能为空");
        }
        if (!StringUtils.hasText(content)){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"内容不能为空");
        }
        if (!StringUtils.hasText(targetTypeString) || !DmjiUtils.isPositiveNumber(targetTypeString)){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"类型错误");
        }
        SysMsgTargetType sysMsgTargetType;
        if ((sysMsgTargetType = SysMsgTargetType.byCode(Integer.valueOf(targetTypeString)))==null){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"不存在的类型");
        }

        SysMessage sysMessage = new SysMessage();
        sysMessage.setTitle(title);
        sysMessage.setStatus(Status.NORMAL);
        sysMessage.setPublisherUid(jwtUserInfo.getUid());
        sysMessage.setSysMsgTargetType(sysMsgTargetType.getCode());
        sysMessage.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sysMessage.setContent(content);
        SysMessage insert = sysMessageService.insert(sysMessage);
        logger.debug("插入的新系统消息的标题:{}，对象类型:{}",insert.getTitle(),insert.getSysMsgTargetType());

        return getSuccessResult(sysMessage);
    }

    @DeleteMapping("{smId}")
    @UserLog("删除指定id的系统消息")
    public Result deleteSystemMessage(@PathVariable("smId") Long smid){
        SysMessage sysMessage = sysMessageService.getById(smid);
        if (sysMessage == null){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"该系统消息不存在，无法删除");
        }
        sysMessage.setStatus(Status.DELETE);
        SysMessage update = sysMessageService.update(sysMessage);
        return getSuccessResult(update);
    }

    @PostMapping("/users")
    public Result publishSysMessageToUsers(@RequestBody Map<String,Object> requestMap,
                                           HttpServletRequest request){
        List<Integer> userIds = (List<Integer>) requestMap.get("ids");
        String title = (String) requestMap.get("title");
        String content = (String) requestMap.get("content");

        if (userIds == null || userIds.size()==0){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"用户id不能为空");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"标题或内容不能为空");
        }
        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);
        List<MessageV2> messageV2List = new ArrayList<>();

        userIds.forEach(id->{
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
        return getSuccessResult(insertAll.size(),"ok");
    }

}
