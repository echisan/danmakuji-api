package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.utils.EpisodePageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.model.VideoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cc.dmji.api.utils.DmjiUtils.validatePageParam;
import static cc.dmji.api.web.controller.EpisodeController.generateVideoInfo;

@RestController
@RequestMapping("/admin/episodes")
public class AdminEpisodeController extends BaseController {

    @Autowired
    BangumiService bangumiService;

    @Autowired
    EpisodeService episodeService;

    @GetMapping
    public Result listEpisodes(@RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) Integer pageSize){
        int validateResult = validatePageParam(pageNum,pageSize);
        EpisodePageInfo result = null;
            switch (validateResult){
                case 1:
                    result = episodeService.listEpisodes();
                    break;
                case 2:
                    return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
                case 3:
                    result = episodeService.listEpisodes(pageNum);
                    break;
                case 4:
                    return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"页大小不能为负数");
                case 5:
                    result = episodeService.listEpisodes(pageNum,pageSize);
            }
        if(result == null || result.getEpisodes().size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(result);

    }

    @GetMapping("/{epId}")
    public Result getEpisodeByEpId(@PathVariable Long epId){
        Episode episode = episodeService.getEpisodeByEpId(epId);
        if(null == episode){
           return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"epId不存在");
        }
        return getSuccessResult(episode);
    }

    @GetMapping("/bid/{bangumiId}")
    public Result listEpisodesByBangumiId(@PathVariable Long bangumiId,
                                          @RequestParam(required = false) Integer pageNum,
                                          @RequestParam(required = false) Integer pageSize){
        int validateResult = validatePageParam(pageNum,pageSize);
        EpisodePageInfo result = null;
        if(null == bangumiId){
            return getErrorResult(ResultCode.PARAM_IS_BLANK,"bangumiId不能为空");
        }
        else {
            switch (validateResult){
                case 1:
                    result = episodeService.listEpisodesByBangumiId(bangumiId);
                    break;
                case 2:
                    return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
                case 3:
                    result = episodeService.listEpisodesByBangumiId(bangumiId,pageNum);
                    break;
                case 4:
                    return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"页大小不能为负数");
                case 5:
                    result = episodeService.listEpisodesByBangumiId(bangumiId,pageNum,pageSize);
            }
        }
        if(result == null || result.getEpisodes().size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(result);
    }

    @PostMapping
    public Result addEpisode(@RequestBody Episode episode){
        //若提交的参数中没有bangumiId，则返回错误信息
        if(null == episode.getBangumiId()){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"bangumiId不能为空");
        }
        else {
            //若包含bangumiId,则检测改bangumiId是否存在于bangumi表中
            if(null == bangumiService.getBangumiById(episode.getBangumiId())){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"bangumiId不存在");
            }
        }

        //新增episode不应该提交epId和danmakuId两个参数，应该由后台自动生成
        if(episode.getEpId()!=null || episode.getDanmakuId()!=null){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"插入数据不需要提供epId和danmakuId");
        }

        //剧集索引不能为空
        if(null == episode.getEpIndex()){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"epIndex不能为空");
        }
        else {
            //剧集索引不能为负数
            if(episode.getEpIndex() < 0){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"epIndex不能为负数");
            }
            List<Episode> episodes = episodeService.listAllEpisodesByBangumiId(episode.getBangumiId());
            boolean isEpIndexDup = false;
            for(Episode e:episodes){
                if(e.getEpIndex().equals(episode.getEpIndex())){
                    isEpIndexDup = true;
                }
            }
            if(isEpIndexDup){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"epIndex已存在");
            }
        }

        //若不提交replyable参数，则默认为1（可回复
        if(null != episode.getReplyable()){
            Byte replyable = episode.getReplyable();
            //检查replyable的值是否合法，只能为0或1
            if( replyable.intValue() != 0 && replyable.intValue() !=1){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"replyable的值只能为0和1");
            }
        }
        else {
            //replyable默认值为1
            episode.setReplyable((byte) 1);
        }

        //开始插入数据
        Episode insertedEpisode = episodeService.insertEpisode(episode);
        if(null == insertedEpisode){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"episode添加失败");
        }
        VideoInfo videoInfo = generateVideoInfo(insertedEpisode,bangumiService);
        return getSuccessResult(videoInfo);
    }

    @PutMapping("/{epId}")
    public Result editEpisode(@PathVariable Long epId, @RequestBody Episode episode){

        //根据path中的epId查询是否有对应的数据
        Episode editedEpisode = episodeService.getEpisodeByEpId(epId);

        if(null == editedEpisode){//没有对应的数据，则返回错误信息
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"epId不存在");
        }

        //如果bangumiId参数不为空，则检查bangumiId是否存在于bangumiId表
        if(null != episode.getBangumiId()){
            Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());

            if(null == bangumi){
                //bangumiId不存在于bangumi表，返回错误信息
                return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"bangumiId不存在");
            }
            else {//bangumiId存在与bangumi表，修改bangumiId字段
                editedEpisode.setBangumiId(episode.getBangumiId());
            }
        }

        //epIndex参数不为空
        if(null != episode.getEpIndex()){
            //检查epIndex,保证其不为负数
            if(episode.getEpIndex() < 0){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"epIndex不能为负数");
            }
            else {
                //epIndex合法，更新该字段
                editedEpisode.setEpIndex(episode.getEpIndex());
            }
        }

        //danmakuId不为空
        if( null != episode.getDanmakuId()){
            //检查danmakuId是否合法
            if(episode.getDanmakuId().length() != 32){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"danmakuId非法");
            }
            else {
                editedEpisode.setDanmakuId(episode.getDanmakuId());
            }
        }

        //replyable不为空
        if(null != episode.getReplyable()){
            Byte replyable = episode.getReplyable();
            //检查replyable的值是否合法，只能为0或1
            if( replyable.intValue() != 0 && replyable.intValue() !=1){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"replyable的值只能为0和1");
            }
            else {
                editedEpisode.setReplyable(replyable);
            }
        }
        //更新episode
        editedEpisode = episodeService.updateEpisode(editedEpisode);
        if(null == editedEpisode){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"episode信息更新失败");
        }
        else {
            VideoInfo videoInfo = generateVideoInfo(editedEpisode,bangumiService);
            return getSuccessResult(videoInfo);
        }
    }

    @DeleteMapping("/{epId}")
    public Result deleteEpisode(@PathVariable Long epId){
        Episode episode = episodeService.getEpisodeByEpId(epId);
        if(null == episode){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"删除episode失败");
        }
        else {
            episodeService.deleteEpisode(epId);
            return getSuccessResult(episode);
        }
    }

    @DeleteMapping
    public Result deleteEpisodes(@RequestBody Map<String,List<Episode>> eps){
        List<Episode> episodes = eps.get("episodes");
        List<Long> ids = new ArrayList<>();
        for(Episode ep:episodes){
            if(null == ep.getEpId()){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"epId不能为空");
            }
            ids.add(ep.getEpId());
        }
        List<Episode> deletedEpisodes = episodeService.listEpisodesByEpIds(ids);
        episodeService.deleteEpisodes(episodes);
        return getSuccessResult(deletedEpisodes,"批量是删除成功");
    }

}
