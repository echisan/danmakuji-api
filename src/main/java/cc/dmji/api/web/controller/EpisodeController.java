package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.EpisodePageInfo;
import cc.dmji.api.web.model.VideoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cc.dmji.api.utils.DmjiUtils.validatePageParam;

@RestController
@RequestMapping("/episodes")
public class EpisodeController extends BaseController{

    @Autowired
    EpisodeService episodeService;

    @Autowired
    BangumiService bangumiService;

    @Autowired
    DanmakuService danmakuService;

    /**
     * 根据bangumiId和epIndex查找episode
     * @param bangumiId 番剧id，必须在bangumi表中存在
     * @param epIndex 剧集索引，不能为负数
     * @return 返回的结果可能含有一个或多个episode
     */
    @GetMapping
    public Result listEpisodes(@RequestParam(required = false) Long bangumiId,
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
                    videoInfo.setDanmakuCount(danmakuService.countDanmakuByPlayer(episode.getDanmakuId()));
                    videoInfo.setViewCount(episode.getViewCount());

                    // async
                    updateEpisodeView(episode);

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
    public Result getEpisodeById(@PathVariable Long epId){
        Episode episode = episodeService.getEpisodeByEpId(epId);
        if(null == episode){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        else {
            VideoInfo videoInfo = generateVideoInfo(episode,bangumiService);
            videoInfo.setDanmakuCount(danmakuService.countDanmakuByPlayer(episode.getDanmakuId()));
            videoInfo.setViewCount(episode.getViewCount());

            updateEpisodeView(episode);

            return getSuccessResult(videoInfo);
        }
    }

    @GetMapping("/bid/{bangumiId}")
    public Result listEpisodesByBangumiId(@PathVariable Long bangumiId,
                                          @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                          @RequestParam(required = false, defaultValue = "50") Integer pageSize) {
        int validateResult = validatePageParam(pageNum, pageSize);
        if (null == bangumiId) {
            return getErrorResult(ResultCode.PARAM_IS_BLANK, "bangumiId不能为空");
        }

        if (validateResult != 5) {
            return getErrorResult(ResultCode.PARAM_IS_INVALID, "页码或页数参数不正确");
        }

        EpisodePageInfo episodePageInfo = episodeService.listEpisodesByBangumiId(bangumiId, pageNum, pageSize);
        return getSuccessResult(episodePageInfo);
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
        videoInfo.setThumb(bangumi.getThumb());
        return videoInfo;
    }

    @Async
    public void updateEpisodeView(Episode episode) {
        Long viewCount = episode.getViewCount();
        viewCount = viewCount == null ? 0 : viewCount;
        episode.setViewCount(viewCount + 1);
        Episode updateEpisode = episodeService.updateEpisode(episode);

        Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());
        bangumi.setViewCount(bangumi.getViewCount() == null ? 1 : bangumi.getViewCount());
        Bangumi updateBangumi = bangumiService.updateBangumi(bangumi);
    }


}
