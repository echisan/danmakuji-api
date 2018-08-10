package cc.dmji.api.utils;

import cc.dmji.api.entity.Video;

import java.util.List;

public class VideoPageInfo {

    private List<Video> videos;
    private PageInfo pageInfo;

    public VideoPageInfo(){}

    public VideoPageInfo(List<Video> videos, PageInfo pageInfo) {
        this.videos = videos;
        this.pageInfo = pageInfo;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
