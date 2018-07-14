package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.entity.Video;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.service.VideoService;
import cc.dmji.api.web.model.VideoInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/videos")
public class VideoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    VideoService videoService;

    @Autowired
    EpisodeService episodeService;

    @Autowired
    BangumiService bangumiService;


    @GetMapping("/{videoId}")
    public Result getVideoById(@PathVariable String videoId) {
        Video video = videoService.getVideoByVideoId(videoId);
        if (null == video) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        } else {
            return getSuccessResult(video);
        }
    }

    @GetMapping("/{fileSize}/{vMd5}")
    public Result getVideoByFileSizeAndVmd5(@PathVariable(value = "fileSize") Long fileSize,
                                            @PathVariable(value = "vMd5") String vMd5) {
        logger.info("fileSize: [ {} ] , md5: [ {} ]", fileSize, vMd5);
        List<Video> videos = videoService.getVideoByFileSizeAndVmd5(fileSize, vMd5);
        if (null == videos || videos.size() == 0) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        } else {
            List<VideoInfo> videoInfos = new ArrayList<>();
            videos.forEach(video -> {
                Integer epId = video.getEpId();
                //根据episodeId获取episode
                Episode episode = episodeService.getEpisodeByEpId(epId);
                //根据bangumiId获取bangumi
                Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());
                VideoInfo videoInfo = new VideoInfo();
                //设置弹幕id
                videoInfo.setDanmakuId(episode.getDanmakuId());
                //设置episodeId
                videoInfo.setEpisodeId(episode.getEpId());
                //设置番剧id
                videoInfo.setBangumiId(episode.getBangumiId());
                //设置episode索引(第几集)
                videoInfo.setEpisodeIndex(episode.getEpIndex());
                //设置是否可回复
                videoInfo.setReplyable(episode.getReplyable());
                //设置番剧名称
                videoInfo.setBangumiName(bangumi.getBangumiName());
                videoInfos.add(videoInfo);
            });
            return getSuccessResult(videoInfos);
        }
    }

    // 接收用户选择的视频的信息并保存到数据库
    @PostMapping("/videoMatchInfo")
    public Result saveVideoMatchInfo(@RequestBody Map<String, String> requestMap) {

        Integer epId = Integer.valueOf(requestMap.get("ep_id"));
        Long fileSize = Long.valueOf(requestMap.get("v_size"));
        String md5 = requestMap.get("v_md5");
        logger.debug("epId:{},fileSize:{},md5:{}", epId, fileSize, md5);

        Video video = new Video();
        video.setEpId(epId);
        video.setFileSize(fileSize);
        video.setvMd5(md5);
        Video newVideo = videoService.insertVideo(video);
        logger.debug(newVideo.toString());
        return getSuccessResult();
    }

    // 记录视频识别错误信息
    @PostMapping("/matchFail")
    public Result saveMatchFailInfo() {
        return getSuccessResult();
    }

}
