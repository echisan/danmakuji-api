package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Notice;
import cc.dmji.api.service.NoticeService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.GeneralUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/7/22
 */
@RequestMapping("/admin/notices")
@RestController
public class AdminNoticeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminNoticeController.class);

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public ResponseEntity<Result> listNotices(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                              @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps){
        if (DmjiUtils.validatePageParam(pn, ps) != 5) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "页码页数参数不正确");
        }

        Page<Notice> noticePage = noticeService.listNotices(pn, ps);
        List<Notice> noticeList = noticePage.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("notices", noticeList);
        data.put("page", new PageInfo(pn, ps, noticePage.getTotalElements()));
        return getResponseEntity(HttpStatus.OK,getSuccessResult(data));
    }

    @RequestMapping(method = {RequestMethod.POST,RequestMethod.PUT})
    public ResponseEntity<Result> postNotice(@RequestBody Map<String,String> requestMap,
                                             HttpServletRequest request){
        String title = requestMap.get("title");
        String content = requestMap.get("content");
        String isShowIndex = requestMap.get("isi");

        if (!StringUtils.hasText(title)){
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST,ResultCode.PARAM_IS_INVALID,"标题不能为空");
        }
        if (!StringUtils.hasText(content)){
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST,ResultCode.PARAM_IS_INVALID,"内容不能为空");
        }

        if (!isShowIndex.equals("0") && !isShowIndex.equals("1")){
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST,ResultCode.PARAM_IS_INVALID,"isi只接受0或1");
        }


        if (isShowIndex.equals("1")){
            // 首先将别的置顶的换下来
            Notice indexNotice = noticeService.getNoticeByShowIndex();
            if (indexNotice != null){
                indexNotice.setIsShowIndex((byte)0);
                noticeService.updateNotice(indexNotice);
            }
        }

        if (request.getMethod().equalsIgnoreCase("POST")){
            Long userId = getUidFromRequest(request);
            Notice notice = new Notice();
            notice.setUserId(userId);
            notice.setContent(content);
            notice.setTitle(title);
            notice.setIsShowIndex(Byte.valueOf(isShowIndex));
            Notice insertNotice = noticeService.insertNotice(notice);
            logger.debug("insertNotice:{}",insertNotice);
            return getSuccessResponseEntity(getSuccessResult());
        } else {
            String nidString = requestMap.get("nid");
            if (!StringUtils.hasText(nidString)){
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST,ResultCode.PARAM_IS_INVALID,"nid不能为空");
            }
            if (!DmjiUtils.isPositiveNumber(nidString)){
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST,ResultCode.PARAM_IS_INVALID,"nid类型有误");
            }
            Notice notice = noticeService.getNoticeById(Long.valueOf(nidString));
            if (null == notice){
                return getErrorResponseEntity(HttpStatus.NOT_FOUND,ResultCode.RESULT_DATA_NOT_FOUND,"Notice找不到,id："+Long.valueOf(nidString));
            }

            notice.setIsShowIndex(Byte.valueOf(isShowIndex));
            notice.setTitle(title);
            notice.setContent(content);
            Notice updateNotice = noticeService.updateNotice(notice);
            logger.debug("updateNotice:{}",updateNotice);
            return getSuccessResponseEntity(getSuccessResult());
        }
    }

    @DeleteMapping("/{nid}")
    public ResponseEntity<Result> deleteNotice(@PathVariable("nid")Long noticeId){
        Notice notice = noticeService.getNoticeById(noticeId);
        if (null == notice){
            return getErrorResponseEntity(HttpStatus.NOT_FOUND,ResultCode.RESULT_DATA_NOT_FOUND,"Notice找不到,id:"+noticeId);
        }
        noticeService.deleteNoticeById(notice.getId());
        return getSuccessResponseEntity(getSuccessResult("删除成功"));
    }
}
