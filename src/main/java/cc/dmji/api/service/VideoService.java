package cc.dmji.api.service;

import cc.dmji.api.entity.Video;
import cc.dmji.api.utils.VideoPageInfo;

import java.util.List;

public interface VideoService {

    List<Video> getVideoByFileSizeAndVmd5(Long fileSize, String md5);

    List<Video> listVideoByFileSizeAndVmd5SortByScore(Long fileSize, String md5);

    Video getMatchVideoByFileSizeAndMd5(Long fileSize, String md5);

    Video getVideoByVideoId(Long videoId);

    VideoPageInfo listVideos();

    VideoPageInfo listVideos(int pageNum);

    VideoPageInfo listVideos(int pageNum, int pageSize);

    VideoPageInfo listVideosByEpId(Long epId);

    VideoPageInfo listVideosByEpId(Long epId, int pageNum);

    VideoPageInfo listVideosByEpId(Long epId, int pageNum, int pageSize);

    List<Video> listVideosByEpIds(List<Long> ids);

    Video insertVideo(Video video);

    Video updateVideo(Video video);

    void deleteVideoById(Long videoId);

    void deleteVideos(List<Video> videos);

    Long countVideo();

    VideoPageInfo listVideoByEpIdAndIsMatch(Long epId, Byte isMatch, int pn, int ps);

    VideoPageInfo listVideoByIsMatch(Byte isMatch, int pn, int ps);

}
