package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.web.model.VideoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/episodes")
public class EpisodeController extends BaseController{

    @Autowired
    EpisodeService episodeService;

    @Autowired
    BangumiService bangumiService;

    /**
     * 根据bangumiId和epIndex查找episode
     * @param bangumiId 番剧id，必须在bangumi表中存在
     * @param epIndex 剧集索引，不能为负数
     * @return 返回的结果可能含有一个或多个episode
     */
    @GetMapping
    public Result listEpisodes(@RequestParam(required = false) Integer bangumiId,
                              @RequestParam( required = false) Integer epIndex){
        List<Episode> result = null;//结果集
        List<VideoInfo> videoInfos = new ArrayList<>();
        //参数bangumiId不为空，则根据bangumiId查找episode
        if (bangumiId != null){
            //参数epIndex不为空，则根据bangumiId和epIndex查找
            if(epIndex!=null){
                Episode episode = episodeService.getEpisodeByBangumiIdAndEpIndex(bangumiId,epIndex);
                if(null == episode){
                    //没有查找到数据
                    return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
                }
                else {
                    //查找到数据
                    VideoInfo videoInfo = generateVideoInfo(episode);
                    return getSuccessResult(videoInfo);
                }
            }
            else {
                //参数epIndex为空，只根据bangumiId查找，结果可能含有多个episode
                result = episodeService.listEpisodesByBangumiId(bangumiId);
                result.forEach(e->{
                    videoInfos.add(generateVideoInfo(e));
                });
            }
        }
        else {
            //没有任何参数，默认查找所有episode
            result = episodeService.listEpisodes();
            result.forEach(e->{
                videoInfos.add(generateVideoInfo(e));
            });
        }
        if(videoInfos.size() == 0){
            //没有数据
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(videoInfos);
    }

    /**
     * 根据epId查找episode
     * @param epId
     * @return 返回结果中最多只包含一个episode
     */
    @GetMapping("/{epId}")
    public Result getEpisodeById(@PathVariable Integer epId){
        Episode episode = episodeService.getEpisodeByEpId(epId);
        if(null == episode){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        else {
            VideoInfo videoInfo = generateVideoInfo(episode);
            return getSuccessResult(episode);
        }
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
        VideoInfo videoInfo = generateVideoInfo(insertedEpisode);
        return getSuccessResult(videoInfo);
    }

    @PutMapping("/{epId}")
    public Result editEpisode(@PathVariable Integer epId, @RequestBody Episode episode){

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
            VideoInfo videoInfo = generateVideoInfo(editedEpisode);
            return getSuccessResult(videoInfo);
        }
    }

    @DeleteMapping("/{epId}")
    public Result deleteEpisode(@PathVariable Integer epId){
        Episode episode = episodeService.getEpisodeByEpId(epId);
        if(null == episode){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"删除episode失败");
        }
        else {
            episodeService.deleteEpisode(epId);
            return getSuccessResult(episode);
        }
    }

    private VideoInfo generateVideoInfo(Episode episode){
        VideoInfo videoInfo = new VideoInfo();
        Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());
        if(null == bangumi){
            throw new RuntimeException("bangumiId不存在");
        }
        videoInfo.setBangumiName(bangumi.getBangumiName());
        videoInfo.setBangumiId(episode.getBangumiId());
        videoInfo.setEpisodeIndex(episode.getEpIndex());
        videoInfo.setReplyable(episode.getReplyable());
        videoInfo.setDanmakuId(episode.getDanmakuId());
        videoInfo.setEpisodeId(episode.getEpId());
        return videoInfo;
    }
}
