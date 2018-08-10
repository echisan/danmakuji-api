package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.utils.BangumiPageInfo;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.EpisodePageInfo;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.model.BangumiInfo;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.dmji.api.utils.DmjiUtils.validatePageParam;

@RestController
@RequestMapping("/bangumis")
public class BangumiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BangumiController.class);

    @Autowired
    BangumiService bangumiService;

    @Autowired
    EpisodeService episodeService;

    @Autowired
    DanmakuService danmakuService;

    @GetMapping
    public Result listBangumis(@RequestParam(required = false) String bangumiName,
                               @RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) Integer pageSize) {
        BangumiPageInfo bangumis = null;
        if (null != bangumiName) {
            bangumiName = bangumiName.trim();//去除前后空格
        }
        if (null == bangumiName) {
            if (pageNum != null) {
                if (pageNum < 1) {
                    return getErrorResult(ResultCode.PARAM_IS_INVALID, "页码不能为0或负数");
                } else {
                    if (pageSize != null) {
                        if (pageSize < 1) {
                            return getErrorResult(ResultCode.PARAM_IS_INVALID, "页大小不能为0或负数");
                        } else {
                            bangumis = bangumiService.listBangumis(pageNum, pageSize);
                        }
                    } else {
                        bangumis = bangumiService.listBangumis(pageNum);
                    }
                }
            } else {
                bangumis = bangumiService.listBangumis();
            }

        } else {
            if (!bangumiName.equals("")) {
                bangumiName = "%" + bangumiName + "%";
            }
            if (pageNum != null) {
                if (pageNum < 1) {
                    return getErrorResult(ResultCode.PARAM_IS_INVALID, "页码不能为0或负数");
                } else {
                    if (pageSize != null) {
                        if (pageSize < 1) {
                            return getErrorResult(ResultCode.PARAM_IS_INVALID, "页大小不能为0或负数");
                        } else {
                            bangumis = bangumiService.listBangumisByName(bangumiName, pageNum, pageSize);
                        }
                    } else {
                        bangumis = bangumiService.listBangumisByName(bangumiName, pageNum);
                    }
                }
            } else {
                bangumis = bangumiService.listBangumisByName(bangumiName);
            }
        }
        if (bangumis.getContent().size() == 0) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        } else {
            return getSuccessResult(bangumis);
        }
    }

    @GetMapping("/{id}")
    public Result getBangumiByBangumiId(@PathVariable Long id) {
        Bangumi bangumi = bangumiService.getBangumiById(id);
        if (null == bangumi) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(bangumi);
    }

    @DeleteMapping("/{bangumiId}")
    public Result deleteBangumi(@PathVariable Long bangumiId) {
        Bangumi deletedBangumi = bangumiService.getBangumiById(bangumiId);
        if (null == deletedBangumi) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "删除番剧失败");
        } else {
            bangumiService.deleteBangumiById(bangumiId);
            return getSuccessResult(deletedBangumi);
        }
    }

    @PostMapping("/contribute")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Result> contributeBangumiInfo(@RequestBody Bangumi bangumi) {
        if (StringUtils.isEmpty(bangumi.getBangumiName())) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "番剧名不能为空"));
        }
        if (bangumi.getEpisodeTotal() == null || bangumi.getEpisodeTotal() <= 0) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "番剧名不能为空"));
        }

        return getResponseEntity(HttpStatus.OK, getSuccessResult());
    }


    @GetMapping("/mostView")
    public ResponseEntity<Result> listMostViewBangumi(@RequestParam(value = "pn",required = false,defaultValue = "1") Integer pn,
                                                      @RequestParam(value = "ps",required = false,defaultValue = "4") Integer ps){

        if (DmjiUtils.validatePageParam(pn,ps)!=5){
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST,ResultCode.PARAM_IS_INVALID,"页码页数参数有误");
        }

        Page<Bangumi> bangumiPage = bangumiService.listBangumiOrderByViewCount(pn, ps);
        List<Bangumi> bangumiList = bangumiPage.getContent();
        PageInfo pageInfo = new PageInfo(pn,ps,bangumiPage.getTotalElements());
        Map<String,Object> data = new HashMap<>();
        data.put("bangumi",bangumiList);
        data.put("page",pageInfo);

        return getSuccessResponseEntity(getSuccessResult(data));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleNotUnique() {
        logger.info("局部异常处理----bangumi已存在");
        return getErrorResult(ResultCode.DATA_IS_WRONG, "bangumi已存在");
    }
}
