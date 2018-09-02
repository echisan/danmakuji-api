package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.RequestLimit;
import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Danmaku;
import cc.dmji.api.entity.Report;
import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.ReportHandleStatus;
import cc.dmji.api.enums.ReportReason;
import cc.dmji.api.enums.ReportTargetType;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.service.ReportService;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.utils.JwtUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    @Autowired
    private ReportService reportService;
    @Autowired
    private ReplyV2Service replyV2Service;
    @Autowired
    private DanmakuService danmakuService;

    @PostMapping
    @UserLog("举报")
    @RequestLimit
    public Result postReport(@RequestBody Map<String, Object> requestMap,
                             HttpServletRequest request) {

        Report report = new Report();
        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);
        try {
            Integer reportTargetTypeCode = (Integer) requestMap.get("rtt");
            Integer reportTypeCode = (Integer) requestMap.get("rt");
            Object tid = requestMap.get("tid");
            Long reportTargetId;
            TargetInfo targetInfo;
            if (tid instanceof Integer) {
                reportTargetId = new Integer((int) tid).longValue();
            } else if (tid instanceof Long) {
                reportTargetId = (Long) tid;
            } else {
                return getErrorResult(ResultCode.DATA_IS_WRONG, "参数(tid)类型错误");
            }
            ReportTargetType reportTargetType = ReportTargetType.byCode(reportTargetTypeCode);
            if (reportTargetType == null) {
                return getErrorResult(ResultCode.DATA_IS_WRONG, "参数(rtt)取值不在正确范围内");
            }
            ReportReason reportReason = ReportReason.byCode(reportTypeCode);
            if (reportReason == null) {
                return getErrorResult(ResultCode.DATA_IS_WRONG, "参数(rt)取值不在正确范围内");
            }
            if ((targetInfo = getTargetInfo(reportTargetType, reportTargetId)) == null) {
                return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "找不到被举报的数据信息");
            }
            // init report
            report.setCreateTime(new Timestamp(System.currentTimeMillis()));
            report.setReportTargetId(reportTargetId);
            report.setReportTargetType(reportTargetType.getCode());
            report.setReportReason(reportReason.getCode());
            report.setTargetUserId(targetInfo.targetUserId);
            report.setReportContent(targetInfo.getTargetContent());

            Report dbReport = reportService.get(reportTargetType, reportTargetId, jwtUserInfo.getUid());
            if (dbReport != null) {
                return getErrorResult(ResultCode.DATA_ALREADY_EXIST, "已经举报过啦，不用再举报啦");
            }

        } catch (Exception e) {
            return getErrorResult(ResultCode.DATA_IS_WRONG, "参数类型错误:" + e.getMessage());
        }

        report.setPublisherUserId(jwtUserInfo.getUid());
        report.setReportHandleStatus(ReportHandleStatus.PENDING.getCode());

        // save
        Report insert = reportService.insert(report);
        logger.debug("举报的信息:{}", insert);
        return getSuccessResult("举报成功");
    }

    private TargetInfo getTargetInfo(ReportTargetType reportTargetType, Long reportTargetId) {
        TargetInfo targetInfo = new TargetInfo();
        switch (reportTargetType) {
            case REPLY: {
                ReplyV2 replyV2 = replyV2Service.getById(reportTargetId);
                if (replyV2 == null) {
                    return null;
                }
                targetInfo.setTargetContent(replyV2.getContent());
                targetInfo.setTargetUserId(replyV2.getUserId());
                return targetInfo;
            }
            case DANMAKU: {
                Danmaku danmaku = danmakuService.getById(reportTargetId);
                if (danmaku == null) {
                    return null;
                }
                targetInfo.setTargetUserId(danmaku.getUserId());
                targetInfo.setTargetContent(danmaku.getText());
                return targetInfo;
            }
            default: {
                return null;
            }
        }
    }

    class TargetInfo {
        private Long targetUserId;
        private String targetContent;
        private String targetUrl;

        public Long getTargetUserId() {
            return targetUserId;
        }

        public void setTargetUserId(Long targetUserId) {
            this.targetUserId = targetUserId;
        }

        public String getTargetContent() {
            return targetContent;
        }

        public void setTargetContent(String targetContent) {
            this.targetContent = targetContent;
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public void setTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
        }
    }
}
