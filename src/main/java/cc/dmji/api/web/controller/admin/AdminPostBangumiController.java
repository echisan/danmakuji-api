package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.PostBangumiOrderBy;
import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        PostBangumiStatus postBangumiStatus = PostBangumiStatus.PENDING;
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
        postBangumiInfoList.forEach(pbi->{
            pbi.setPostBangumiStatusName(PostBangumiStatus.valueOf(pbi.getPostBangumiStatus()).getStatusName());
        });
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ps);
        pageInfo.setTotalSize(postBangumiInfoPage.getTotal());

        Map<String,Object> result = new HashMap<>();
        result.put("page",pageInfo);
        result.put("postBangumi",postBangumiInfoList);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(result));

    }
}
