package cc.dmji.api.service;

import cc.dmji.api.entity.Video;
import cc.dmji.api.utils.VideoPageInfo;

import java.util.List;

public interface VideoService {

    Video getVideoByFileSizeAndVmd5(Long fileSize, String md5);

    Video getVideoByVideoId(String videoId);

    VideoPageInfo listVideos();

    VideoPageInfo listVideos(int pageNum);

    VideoPageInfo listVideos(int pageNum,int pageSize);

    VideoPageInfo listVideosByEpId(Integer epId);

    VideoPageInfo listVideosByEpId(Integer epId, int pageNum);

    VideoPageInfo listVideosByEpId(Integer epId, int pageNum, int pageSize);

    List<Video> listVideosByEpIds(List<String> ids);

    Video insertVideo(Video video);

    Video updateVideo(Video video);

    void deleteVideoById(String videoId);

    void deleteVideos(List<Video> videos);

    Long countVideo();
}
