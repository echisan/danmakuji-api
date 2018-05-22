package cc.dmji.api.service;

import cc.dmji.api.entity.Video;

import java.util.List;

public interface VideoService {

    Video getVideoByFileSizeAndVmd5(Long fileSize, String md5);

    Video getVideoByVideoId(String videoId);

    List<Video> listVideos();

    List<Video> listVideosByEpId(Integer epId);

    Video insertVideo(Video video);

    Video updateVideo(Video video);

    void deleteVideoById(String videoId);

    Long countVideo();
}
