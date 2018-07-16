package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.entity.Message;
import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.*;
import cc.dmji.api.service.MessageService;
import cc.dmji.api.service.PostBangumiService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.model.admin.PostBangumiInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/7/12
 */
@RestController
@RequestMapping("/admin/postBangumis")
public class AdminPostBangumiController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminPostBangumiController.class);

    @Autowired
    private PostBangumiService postBangumiService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<Result> listPostBangumis(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                   @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps,
                                                   // 该参数全程是postBangumiStatus
                                                   @RequestParam(value = "pbs", required = false) String pbs,
                                                   // status
                                                   @RequestParam(value = "s", required = false) String s,
                                                   @RequestParam(value = "bt", required = false) Long beginTime,
                                                   @RequestParam(value = "et", required = false) Long endTime) {

        if (DmjiUtils.validatePageParam(pn, ps) != 5) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "pn或ps参数有误");
        }

        Status status = Status.NORMAL;
        PostBangumiStatus postBangumiStatus = null;
        Timestamp bt = null;
        Timestamp et = null;

        try {
            if (s != null) {
                status = Status.valueOf(s.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "参数s错误");
        }

        try {
            if (pbs != null) {
                postBangumiStatus = PostBangumiStatus.valueOf(pbs.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "参数pbs错误");
        }

        if (beginTime != null && endTime != null) {
            bt = new Timestamp(beginTime);
            et = new Timestamp(endTime);
        }

        Status finalStatus = status;
        PostBangumiStatus finalPostBangumiStatus = postBangumiStatus;
        Timestamp finalBt = bt;
        Timestamp finalEt = et;
        Page<PostBangumiInfo> postBangumiInfoPage = PageHelper.startPage(pn, ps, true).doSelectPage(() -> {
            postBangumiService.listPostBangumi(
                    finalStatus,
                    finalPostBangumiStatus,
                    finalBt,
                    finalEt,
                    PostBangumiOrderBy.modifyTime,
                    Direction.DESC);
        });

        List<PostBangumiInfo> postBangumiInfoList = postBangumiInfoPage.getResult();
        postBangumiInfoList.forEach(pbi -> {
            pbi.setPostBangumiStatusName(PostBangumiStatus.valueOf(pbi.getPostBangumiStatus()).getStatusName());
        });
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ps);
        pageInfo.setTotalSize(postBangumiInfoPage.getTotal());

        Map<String, Object> result = new HashMap<>();
        result.put("page", pageInfo);
        result.put("postBangumi", postBangumiInfoList);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(result));

    }

    @PutMapping("/{pbId}")
    public ResponseEntity<Result> postBangumiSuccess(@PathVariable("pbId") Long pbId,
                                                     @RequestBody Map<String, String> requestMap,
                                                     HttpServletRequest request) {

        // 传参格式
        // type: 1 (1:已采纳，2:待完善，3: 不采纳)
        // msg: 修改意见

        PostBangumi postBangumi = postBangumiService.getById(pbId);
        if (postBangumi == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND, "找不到id为" + pbId + "的数据");
        }
        String managerUserId = getUidFromToken(request);
        String type = requestMap.get("type");
        String messageContent;
        switch (type) {
            case "1":
                postBangumi.setPostBangumiStatus(PostBangumiStatus.SUCCESS);
                messageContent = "[" + postBangumi.getBangumiName() + "]，恭喜！你提交的番剧信息已被采纳,可以给番剧设置封面啦~";
                break;
            case "2": {
                String msg = requestMap.get("msg");
                // 如果为空
                if (!StringUtils.hasText(msg)) {
                    return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "到底哪里不行得告诉用户吧,帮助用户修改");
                }
                postBangumi.setPostBangumiStatus(PostBangumiStatus.NEED_PERFECT);
                postBangumi.setMessage(msg);
                messageContent = "[" + postBangumi.getBangumiName() +"]，残念！还差一丢丢就可以了，马上完善一下再次提交吧！";
                break;
            }
            case "3": {
                String msg = requestMap.get("msg");
                // 如果为空
                if (!StringUtils.hasText(msg)) {
                    return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "到底哪里不行得告诉用户吧");
                }
                postBangumi.setPostBangumiStatus(PostBangumiStatus.FAILED);
                postBangumi.setMessage(msg);
                messageContent = "[" + postBangumi.getBangumiName() +"]，很抱歉，您的提交未能采纳，具体原因:" + msg;
                break;
            }
            default: {
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "不支持的type");
            }
        }

        postBangumi.setManagerUserId(managerUserId);
        PostBangumi updatePb = postBangumiService.updatePostBangumi(postBangumi);

        // 发送消息通知改用户
        sendMessage(postBangumi.getUserId(), messageContent);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(updatePb));
    }

    @Async
    public void sendMessage(String userId, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle("番剧提交结果通知");
        message.setContent(content);
        message.setType(MessageType.SYSTEM.name());
        message.setIsRead(MessageConstants.NOT_READ);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        message.setCreateTime(ts);
        message.setModifyTime(ts);
        message.setmStatus(Status.NORMAL.name());
        Message insertMessage = messageService.insertMessage(message);
        logger.debug("send message:{}", insertMessage);
    }
}
