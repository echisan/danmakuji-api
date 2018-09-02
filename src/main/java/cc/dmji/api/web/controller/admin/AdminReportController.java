package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Report;
import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.ReportHandleStatus;
import cc.dmji.api.enums.ReportReason;
import cc.dmji.api.enums.ReportTargetType;
import cc.dmji.api.enums.Status;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.service.ReportService;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.listener.SysMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reports")
public class AdminReportController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminReportController.class);
    @Autowired
    private ReportService reportService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DanmakuService danmakuService;
    @Autowired
    private ReplyV2Service replyV2Service;

    @GetMapping
    public Result listReports(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                              @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps,
                              @RequestParam(value = "type", required = false, defaultValue = "0") Integer type) {

        if (!DmjiUtils.validPageParam(pn, ps)) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "pn,ps取值不合法");
        }
        Page<Report> reports;
        if (type == 0) {
            reports = reportService.listReport(pn, ps);
        } else {
            ReportHandleStatus reportHandleStatus = ReportHandleStatus.byCode(type);
            if (reportHandleStatus == null) {
                return getErrorResult(ResultCode.PARAM_IS_INVALID, "type取值不合法");
            }
            reports = reportService.listByHandleStatus(pn,ps,reportHandleStatus);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("page", new PageInfo(pn, ps, reports.getTotalElements()));

        List<Map<String,Object>> reportList = new ArrayList<>();
        reports.getContent().forEach(report -> {
            Map<String,Object> item = new HashMap<>();
            item.put("handleStatus",report.getReportHandleStatus().getDescription());
            item.put("handledTime",report.getHandledTime());
            item.put("managerId",report.getManagerId());
            item.put("reportReason",report.getReportReason().getDescription());
            item.put("content",report.getReportContent());
            item.put("createTime",report.getCreateTime());
            item.put("targetType",report.getReportTargetType().getDescription());
            item.put("publisherUserId",report.getPublisherUserId());
//            item.put("targetUserId",report.getTargetUserId());
            item.put("id",report.getId());
            reportList.add(item);
        });
        resultMap.put("reports", reportList);
        return getSuccessResult(resultMap);
    }

    @PostMapping("/{reportId}")
    public Result handleReport(@PathVariable("reportId") Long reportId,
                               @RequestBody(required = false) Map<String, Integer> requestMap,
                               HttpServletRequest request) {

        Report report = reportService.getByid(reportId);
        if (report == null) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        } else {
            if (!report.getReportHandleStatus().equals(ReportHandleStatus.PENDING)) {
                return getErrorResult(ResultCode.DATA_ALREADY_EXIST_BUT_ALLOW_REQUEST, "该举报已经被处理了");
            }
        }

        Integer handleType = requestMap.get("handleType");
        if (handleType == null) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "处理类型不能为空");
        }

        ReportHandleStatus reportHandleStatus = ReportHandleStatus.byCode(handleType);
        if (reportHandleStatus == null) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "处理类型参数取值不合法");
        }

        Integer reportReasonCode = requestMap.get("reportReason");
        ReportReason reportReason = ReportReason.byCode(reportReasonCode);
        if (reportReason == null && (!reportHandleStatus.equals(ReportHandleStatus.HANDLED_REJECT))) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "rt参数不正确");
        }

        report.setReportHandleStatus(reportHandleStatus.getCode());
        report.setManagerId(getUidFromRequest(request));
        report.setHandledTime(new Timestamp(System.currentTimeMillis()));
        Report update = reportService.update(report);

        updateReportAndSendMessage(update);
        return getSuccessResult("处理成功");
    }

    @Async
    public void updateReportAndSendMessage(Report report) {
        List<Report> reports = reportService.listReportByTypeAndTargetId(report.getReportTargetType(), report.getReportTargetId());
        if (report.getReportHandleStatus().equals(ReportHandleStatus.HANDLED_ACCEPT)) {
            deleteTarget(report);
            List<Long> uids = new ArrayList<>();
            reports.forEach(r -> uids.add(r.getPublisherUserId()));
            String title = "举报结果通知";
            String content = "您举报的内容【" + DmjiUtils.formatReplyContent(report.getReportContent()) +
                    "】因【" + report.getReportReason().getDescription() + "】已被删除。感谢您对darker社区秩序的维护，deep♂dark♂fantasy~";
            applicationContext.publishEvent(new SysMessageEvent(this, uids, title, content));
        }
        reports.forEach(report1 -> {
            report1.setReportHandleStatus(report.getReportHandleStatus().getCode());
            report1.setCreateTime(report.getHandledTime());
            report1.setManagerId(report.getManagerId());
        });
        List<Report> update = reportService.update(reports);
        logger.debug("已同步处理同一内容的其他举报:", update.size());
    }

    @Async
    public void deleteTarget(Report report){
        switch (report.getReportTargetType()){
            case DANMAKU:{
                danmakuService.deleteById(report.getReportTargetId());
                break;
            }
            case REPLY:{
                ReplyV2 replyV2 = replyV2Service.getById(report.getReportTargetId());
                replyV2.setStatus(Status.DELETE);
                replyV2Service.update(replyV2);
                break;
            }
            default:{
                break;
            }
        }
    }
}
