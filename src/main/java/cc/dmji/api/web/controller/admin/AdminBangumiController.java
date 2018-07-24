package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.utils.BangumiPageInfo;
import cc.dmji.api.web.controller.BaseController;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cc.dmji.api.utils.DmjiUtils.validatePageParam;

@RestController
@RequestMapping("/admin/bangumis")
public class AdminBangumiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminBangumiController.class);

    @Autowired
    BangumiService bangumiService;

    @Autowired
    EpisodeService episodeService;

    @GetMapping
    public Result listBangumis(@RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) String bangumiName){
        int validateResult = validatePageParam(pageNum,pageSize);
        BangumiPageInfo bangumiPageInfo = null;
        if(null == bangumiName){
            switch (validateResult){
                case 1:
                    bangumiPageInfo = bangumiService.listBangumis();
                    break;
                case 2:
                    return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
                case 3:
                    bangumiPageInfo = bangumiService.listBangumis(pageNum);
                    break;
                case 4:
                    return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"页大小不能为负数");
                case 5:
                    bangumiPageInfo = bangumiService.listBangumis(pageNum,pageSize);
            }
        }
        else {
            bangumiName = bangumiName.trim();//去除前后空格
            if(!bangumiName.equals("")){
                bangumiName = "%"+bangumiName+"%";
            }
            switch (validateResult){
                case 1:
                    bangumiPageInfo = bangumiService.listBangumisByName(bangumiName);
                    break;
                case 2:
                    return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
                case 3:
                    bangumiPageInfo = bangumiService.listBangumisByName(bangumiName,pageNum);
                    break;
                case 4:
                    return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"页大小不能为负数");
                case 5:
                    bangumiPageInfo = bangumiService.listBangumisByName(bangumiName,pageNum,pageSize);
            }
        }
        if(bangumiPageInfo == null || bangumiPageInfo.getContent().size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(bangumiPageInfo);
    }

    @GetMapping("/{id}")
    public Result getBangumiById(@PathVariable Long id){
        Bangumi bangumi = bangumiService.getBangumiById(id);
        if(null == bangumi){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(bangumi);
    }

    @PostMapping
    public Result addBangumi(@RequestBody Bangumi bangumi){
        if(null == bangumi.getBangumiName() || bangumi.getBangumiName().equals("")){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"BangumiName不能为空");
        }
        if(null == bangumi.getEpisodeTotal() || bangumi.getEpisodeTotal()<0){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"集数不能为负数");
        }
        if(null != bangumi.getBangumiId()){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"非法参数(插入数据不需要提供id)");
        }
        // TODO 图片链接验证

        Bangumi insertedBangumi = null;
        insertedBangumi = bangumiService.insertBangumi(bangumi);
        if(null == insertedBangumi){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"添加番剧信息失败");
        }
        else {
            List<Episode> episodes = new ArrayList<>(insertedBangumi.getEpisodeTotal());
            for(int i = 1;i <= insertedBangumi.getEpisodeTotal(); i++){
                Episode episode = new Episode();
                episode.setBangumiId(insertedBangumi.getBangumiId());
                episode.setEpIndex(i);
                episode.setReplyable((byte) 1);
                episodes.add(episode);
            }
            episodeService.insertEpisodes(episodes);
            return getSuccessResult(insertedBangumi);
        }
    }

    @PutMapping("/{bangumiId}")
    public Result editBangumi(@PathVariable("bangumiId") Long bangumiId, @RequestBody Bangumi bangumi){

        Bangumi editedBangumi = bangumiService.getBangumiById(bangumiId);
        if(editedBangumi == null){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"BangumiId不存在");
        }
        if(null != bangumi.getBangumiName()){
            if(bangumi.getBangumiName().equals("")){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"BangumiName不能为空");
            }
            else {
                editedBangumi.setBangumiName(bangumi.getBangumiName());
            }
        }
        if(null != bangumi.getEpisodeTotal()){
            if(bangumi.getEpisodeTotal() < 0){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"番剧集数不能为空或负数");
            }
            else {
                editedBangumi.setEpisodeTotal(bangumi.getEpisodeTotal());
            }
        }
        // TODO 图片链接验证
        if(bangumi.getThumb() == null || bangumi.getThumb().equals("")){}
        else {
            editedBangumi.setThumb(bangumi.getThumb());
        }
        editedBangumi.setModifyTime(new Timestamp(System.currentTimeMillis()));
        editedBangumi = bangumiService.updateBangumi(editedBangumi);
        if(null == editedBangumi){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"更新番剧信息失败");
        }
        else {
            return getSuccessResult(editedBangumi);
        }
    }

    @DeleteMapping("/{bangumiId}")
    public Result deleteBangumi(@PathVariable Long bangumiId){
        Bangumi deletedBangumi = bangumiService.getBangumiById(bangumiId);
        if(null == deletedBangumi){
            return getErrorResult(ResultCode.PARAM_IS_INVALID,"删除番剧失败");
        }
        else {
            bangumiService.deleteBangumiById(bangumiId);
            return getSuccessResult(deletedBangumi,"删除番剧成功");
        }
    }

    @DeleteMapping
    public Result deleteBangumis(@RequestBody Map<String,List<Bangumi>> bgs) throws IOException {
        List<Bangumi> bangumis = bgs.get("bangumis");
        List<Long> ids = new ArrayList<>();
        for(Bangumi b:bangumis){
            if(b.getBangumiId() == null){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"番剧id不能为空");
            }
            ids.add(b.getBangumiId());
        }
        List<Bangumi> deleteBangumis = bangumiService.getBangumisByIds(ids);
        bangumiService.deleteBangumis(bangumis);
        return getSuccessResult(deleteBangumis,"批量删除成功");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleNotUnique(){
        logger.info("局部异常处理----bangumi已存在");
        return getErrorResult(ResultCode.DATA_IS_WRONG,"bangumi已存在");
    }
}
