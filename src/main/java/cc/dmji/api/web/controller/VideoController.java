package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.entity.Video;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.DanmakuService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.service.VideoService;
import cc.dmji.api.web.model.VideoInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/videos")
public class VideoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private BangumiService bangumiService;

    @Autowired
    private DanmakuService danmakuService;


    @GetMapping("/{videoId}")
    public Result getVideoById(@PathVariable Long videoId) {
        Video video = videoService.getVideoByVideoId(videoId);
        if (null == video) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        } else {
            return getSuccessResult(video);
        }
    }

    @GetMapping("/{fileSize}/{vMd5}")
    @UserLog("识别视频")
    public Result getVideoByFileSizeAndVmd5(@PathVariable(value = "fileSize") Long fileSize,
                                            @PathVariable(value = "vMd5") String vMd5) {
        logger.info("fileSize: [ {} ] , md5: [ {} ]", fileSize, vMd5);
        List<Video> videos = videoService.listVideoByFileSizeAndVmd5SortByScore(fileSize, vMd5);

        if (null == videos || videos.size() == 0) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        } else {
            Map<String, Object> data = new HashMap<>();
            Video expectVideo = null;
            // 判断是否有 isMatch == 1
            for (Video v : videos) {
                if (v.getIsMatch().equals((byte)1)){
                    expectVideo = v;
                    break;
                }
            }
            if (expectVideo == null){
                // 判断score进行识别
                // 如果查出2个或以上的数据
                if (videos.size() >= 2) {
                    // score最高
                    Integer score1 = videos.get(0).getScore();
                    // score第二高
                    Integer score2 = videos.get(1).getScore();

                    // 如果最高的score是第二的两倍
                    if (score1 - score2 > 20) {
                        expectVideo = videos.get(0);
                    }
                }
            }

            List<VideoInfo> videoInfos = new ArrayList<>();
            Video finalExpectVideo = expectVideo;
            videos.forEach(video -> {
                Long epId = video.getEpId();
                //根据episodeId获取episode
                Episode episode = episodeService.getEpisodeByEpId(epId);
                //根据bangumiId获取bangumi
                Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());
                // 根据episodeId获取弹幕
                Long danmakuCount = danmakuService.countDanmakuByPlayer(episode.getDanmakuId());

                VideoInfo videoInfo = new VideoInfo();
                //设置弹幕id
                videoInfo.setDanmakuId(episode.getDanmakuId());
                // 设置弹幕大小
                videoInfo.setDanmakuCount(danmakuCount);
                //设置episodeId
                videoInfo.setEpisodeId(episode.getEpId());
                //设置番剧id
                videoInfo.setBangumiId(episode.getBangumiId());
                //设置episode索引(第几集)
                videoInfo.setEpisodeIndex(episode.getEpIndex());
                // 设置这一集有多少访问量
                videoInfo.setViewCount(episode.getViewCount() == null ? 0L : episode.getViewCount());
                //设置是否可回复
                videoInfo.setReplyable(episode.getReplyable());
                //设置番剧名称
                videoInfo.setBangumiName(bangumi.getBangumiName());
                // 设置videoId
                videoInfo.setVideoId(video.getVideoId());
                // 设置封面
                videoInfo.setThumb(bangumi.getThumb());

                if (finalExpectVideo != null) {
                    if (finalExpectVideo.getVideoId().equals(video.getVideoId())) {
                        data.put("ev", videoInfo);
                    }
                }
                videoInfos.add(videoInfo);
            });

            if (finalExpectVideo != null) {
                // 如果是存在expectVideo的话就更新该集的阅读量
                updateEpisodeView(finalExpectVideo.getEpId());
            }
            if (videoInfos.size() == 1) {
                updateEpisodeView(videoInfos.get(0).getEpisodeId());
            }

            data.put("videoInfos", videoInfos);
            return getSuccessResult(data);
        }
    }

    // 接收用户选择的视频的信息并保存到数据库
    @PostMapping("/videoMatchInfo")
    public Result saveVideoMatchInfo(@RequestBody Map<String, String> requestMap) {

        Long epId = Long.valueOf(requestMap.get("ep_id"));
        Long fileSize = Long.valueOf(requestMap.get("v_size"));
        String md5 = requestMap.get("v_md5");
        logger.debug("epId:{},fileSize:{},md5:{}", epId, fileSize, md5);

        Video video = new Video();
        video.setEpId(epId);
        video.setFileSize(fileSize);
        video.setvMd5(md5);
        video.setScore(1);
        Video newVideo = videoService.insertVideo(video);
        logger.debug(newVideo.toString());

        // 更新集数的点击量
        updateEpisodeView(video.getEpId());

        return getSuccessResult();
    }

    // 记录视频识别错误信息
    @PostMapping("/matchFail")
    public Result saveMatchFailInfo() {
        return getSuccessResult();
    }

    // 记录时评识别成功信息
    @PostMapping("/matchSuccess")
    public Result updateVideoScore(@RequestBody Map<String, Long> requestMap) {
        Long videoId = requestMap.get("v");
        Video video = videoService.getVideoByVideoId(videoId);
        if (video == null) {
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        video.setScore(video.getScore() + 1);
        Video updateVideo = videoService.updateVideo(video);
        logger.debug("updateVideo,{}", updateVideo);

        // 异步处理点击
        updateEpisodeView(video.getEpId());
        return getSuccessResult();
    }

    @Async
    public void updateEpisodeView(Long epId) {
        Episode episode = episodeService.getEpisodeByEpId(epId);
        Long viewCount = episode.getViewCount();
        viewCount = viewCount == null ? 0 : viewCount;
        episode.setViewCount(viewCount + 1);
        Episode updateEpisode = episodeService.updateEpisode(episode);

        Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());
        bangumi.setViewCount(bangumi.getViewCount() == null ? 1 : bangumi.getViewCount());
        Bangumi updateBangumi = bangumiService.updateBangumi(bangumi);

        logger.debug("update episode view count {}, update Bangumi view Count {}", updateEpisode, updateBangumi);
    }

}
