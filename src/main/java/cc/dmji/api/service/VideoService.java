package cc.dmji.api.service;

import cc.dmji.api.entity.Video;
import cc.dmji.api.utils.VideoPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VideoService {

    List<Video> getVideoByFileSizeAndVmd5(Long fileSize, String md5);

    List<Video> listVideoByFileSizeAndVmd5SortByScore(Long fileSize,String md5);

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

    VideoPageInfo listVideoByEpIdAndIsMatch(Integer epId, Byte isMatch, int pn, int ps);

    VideoPageInfo listVideoByIsMatch(Byte isMatch,int pn,int ps);
}
