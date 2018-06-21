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
                    VideoInfo videoInfo = generateVideoInfo(episode,bangumiService);
                    return getSuccessResult(videoInfo);
                }
            }
            else {
                //参数epIndex为空，只根据bangumiId查找，结果可能含有多个episode
                result = episodeService.listAllEpisodesByBangumiId(bangumiId);
                result.forEach(e->{
                    videoInfos.add(generateVideoInfo(e,bangumiService));
                });
            }
        }
        else {
            //没有任何参数，默认查找所有episode
//            result = episodeService.listEpisodes();
//            result.forEach(e->{
//                videoInfos.add(generateVideoInfo(e,bangumiService));
//            });
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
            VideoInfo videoInfo = generateVideoInfo(episode,bangumiService);
            return getSuccessResult(videoInfo);
        }
    }



    public static VideoInfo generateVideoInfo(Episode episode, BangumiService bangumiService){
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
