package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.entity.Video;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.service.VideoService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.VideoPageInfo;
import cc.dmji.api.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/videos")
public class AdminVideoController extends BaseController {

    @Autowired
    VideoService videoService;

    @Autowired
    EpisodeService episodeService;


    @GetMapping
    public Result listVideos(@RequestParam(required = false) Integer pageNum,
                             @RequestParam(required = false) Integer pageSize){
        VideoPageInfo result = null;
        int validateResult = DmjiUtils.validatePageParam(pageNum,pageSize);
        switch (validateResult){
            case 1:
                result = videoService.listVideos();
                break;
            case 2:
                return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
            case 3:
                result = videoService.listVideos(pageNum);
                break;
            case 4:
                return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"页大小不能为负数");
            case 5:
                result = videoService.listVideos(pageNum,pageSize);

        }
        if(null == result || result.getVideos().size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(result);
    }

    @GetMapping("/eid/{epId}")
    public Result listVideosByEpId(@PathVariable Integer epId,
                                   @RequestParam(required = false) Integer pageNum,
                                   @RequestParam(required = false) Integer pageSize){
        VideoPageInfo result = null;
        int validateResult = DmjiUtils.validatePageParam(pageNum,pageSize);
        switch (validateResult){
            case 1:
                result = videoService.listVideosByEpId(epId);
                break;
            case 2:
                return getErrorResult(ResultCode.PARAM_IS_INVALID,"页码不能为0或负数");
            case 3:
                result = videoService.listVideosByEpId(epId,pageNum);
                break;
            case 4:
                return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"页大小不能为负数");
            case 5:
                result = videoService.listVideosByEpId(epId,pageNum,pageSize);
        }
        if(null == result || result.getVideos().size() == 0){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND);
        }
        return getSuccessResult(result);
    }

    @PostMapping
    public Result addVideo(@RequestBody Video video){
        if(null == video.getEpId()){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"epId不能为空");
        }
        else {
            Episode episode = episodeService.getEpisodeByEpId(video.getEpId());
            if(null == episode){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"epId不存在");
            }
        }

        if(null == video.getvMd5() || video.getvMd5().equals("")){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"vMd5不能为空");
        }

        if(null == video.getFileSize()){
            return  getErrorResult(ResultCode.DATA_IS_WRONG,"文件大小不能为空");
        }
        else {
            if(video.getFileSize() < 0){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"文件大小不能为负数");
            }
        }

        if( video.getVideoId() != null){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"插入数据不需要提供videoId");
        }

        Video insertedVideo = videoService.insertVideo(video);
        if(null == insertedVideo){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"插入video失败");
        }
        return getSuccessResult(insertedVideo);
    }

    @PutMapping("/{videoId}")
    public Result editVideo(@PathVariable String videoId, @RequestBody Video video){
        Video editedVideo = videoService.getVideoByVideoId(videoId);
        if(null == editedVideo){
            return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"videoId不存在");
        }

        if( video.getEpId()!= null){
            if(null == episodeService.getEpisodeByEpId(video.getEpId())){
                return getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND,"epId不存在");
            }
            else {
                editedVideo.setEpId(video.getEpId());
            }
        }

        if(null != video.getvMd5()){
            if(video.getvMd5().equals("")){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"vMd5不能为空");
            }
            else {
                editedVideo.setvMd5(video.getvMd5());
            }
        }

        if(null != video.getFileSize()){
            if(video.getFileSize() < 0){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"fileSize不能为负数");
            }
            else {
                editedVideo.setFileSize(video.getFileSize());
            }
        }

        editedVideo = videoService.updateVideo(editedVideo);
        if(null == editedVideo){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"video信息更新失败");
        }
        else {
            return getSuccessResult(editedVideo);
        }
    }

    @DeleteMapping("/{videoId}")
    public Result deleteVideo(@PathVariable String videoId){
        Video video = videoService.getVideoByVideoId(videoId);
        if(null == video){
            return getErrorResult(ResultCode.DATA_IS_WRONG,"video删除失败");
        }
        else {
            videoService.deleteVideoById(videoId);
            return getSuccessResult(video);
        }
    }

    @DeleteMapping
    public Result deleteVideos(@RequestBody Map<String,List<Video>> vds){
        List<Video> videos = vds.get("videos");
        List<String> ids = new ArrayList<>();
        for(Video v:videos){
            if(null == v.getVideoId()){
                return getErrorResult(ResultCode.DATA_IS_WRONG,"批量删除失败，videoId不能为空");
            }
            ids.add(v.getVideoId());
        }
        List<Video> deletedVideos = videoService.listVideosByEpIds(ids);
        videoService.deleteVideos(videos);
        return getSuccessResult(deletedVideos,"批量删除成功");
    }

}
