package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Notice;
import cc.dmji.api.service.NoticeService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/7/22
 */
@RestController
@RequestMapping("/notices")
public class NoticeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/showIndex")
    public ResponseEntity<Result> getShowIndexNotice() {
        Notice notice = noticeService.getNoticeByShowIndex();
        Map<String, Object> resultMap = new HashMap<>();
        if (notice != null) {
            notice.setUserId(null);
            notice.setContent("");
            resultMap.put("indexNotice", notice);
        } else {
            resultMap.put("indexNotice", null);
        }
        return getSuccessResponseEntity(getSuccessResult(resultMap));
    }

    @GetMapping
    @UserLog("获取公告列表")
    public ResponseEntity<Result> listNotice(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                             @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps) {

        if (DmjiUtils.validatePageParam(pn, ps) != 5) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "页码页数参数不正确");
        }

        Page<Notice> noticePage = noticeService.listNotices(pn, ps);
        List<Notice> noticeList = noticePage.getContent();
        noticeList.forEach(notice -> notice.setUserId(null));

        Map<String, Object> data = new HashMap<>();
        data.put("notices", noticeList);
        data.put("page", new PageInfo(pn, ps, noticePage.getTotalElements()));
        return getSuccessResponseEntity(getSuccessResult(data));
    }

    @GetMapping("/{nid}")
    @UserLog("获取公告详情")
    public ResponseEntity<Result> getNoticeById(@PathVariable("nid") Long noticeId) {

        Notice notice = noticeService.getNoticeById(noticeId);
        if (null == notice) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
        }
        notice.setUserId(null);
        // async
        updateNoticeViewCount(notice);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(notice,"OK"));
    }

    @Async
    public void updateNoticeViewCount(Notice notice) {
        noticeService.updateNoticeViewCountById(notice);
    }

}
