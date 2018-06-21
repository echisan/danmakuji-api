package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.utils.BangumiPageInfo;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bangumis")
public class BangumiController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(BangumiController.class);

    @Autowired
    BangumiService bangumiService;

    @Autowired
    EpisodeService episodeService;

    @GetMapping
    public Result listBangumis(@RequestParam(required = false) String bangumiName,
                               @RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) Integer pageSize){
        BangumiPageInfo bangumis = null;
        if(null != bangumiName){
            bangumiName = bangumiName.trim();//去除前后空格
        }
        if(null == bangumiName){
            if(pageNum != null){
                if(pageNum < 1){
                    return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
                }
                else {
                    if( pageSize != null){
                        if(pageSize < 1){
                            return getErrorResult(ResultCode.PARAM_IS_INVALID,"页大小不能为0或负数");
                        }
                        else {
                            bangumis = bangumiService.listBangumis(pageNum,pageSize);
                        }
                    }
                    else {
                        bangumis = bangumiService.listBangumis(pageNum);
                    }
                }
            }
            else {
                bangumis = bangumiService.listBangumis();
            }

        }
        else {
            if(!bangumiName.equals("")){
                bangumiName = "%"+bangumiName+"%";
            }
            if(pageNum != null){
                if(pageNum < 1){
                    return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
                }
                else {
                    if(pageSize != null){
                        if(pageSize < 1){
                            return getErrorResult(ResultCode.PARAM_IS_INVALID,"页大小不能为0或负数");
                        }
                        else {
                            bangumis = bangumiService.listBangumisByName(bangumiName,pageNum,pageSize);
                        }
                    }
                    else {
                        bangumis = bangumiService.listBangumisByName(bangumiName,pageNum);
                    }
                }
            }
            else {
                bangumis = bangumiService.listBangumisByName(bangumiName);
            }
        }
        if(bangumis.getContent().size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        else {
            return getSuccessResult(bangumis);
        }
    }

    @GetMapping("/{id}")
    public Result getBangumiByBangumiId(@PathVariable Integer id){
        Bangumi bangumi = bangumiService.getBangumiById(id);
        if(null == bangumi){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(bangumi);
    }

    @DeleteMapping("/{bangumiId}")
    public Result deleteBangumi(@PathVariable Integer bangumiId){
        Bangumi deletedBangumi = bangumiService.getBangumiById(bangumiId);
        if(null == deletedBangumi){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"删除番剧失败");
        }
        else {
            bangumiService.deleteBangumiById(bangumiId);
            return getSuccessResult(deletedBangumi);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleNotUnique(){
        logger.info("局部异常处理----bangumi已存在");
        return getErrorResult(ResultCode.DATA_IS_WRONG,"bangumi已存在");
    }
}
