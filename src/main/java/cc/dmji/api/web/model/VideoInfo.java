package cc.dmji.api.web.model;

public class VideoInfo {

    private String danmakuId;
    private Long episodeId;
    private String bangumiName;
    private Long bangumiId;
    private Integer episodeIndex;
    private Byte replyable;
    private Long videoId;
    private String thumb;
    private Long viewCount;
    private Long danmakuCount;

    public Integer getEpisodeIndex() {
        return episodeIndex;
    }

    public void setEpisodeIndex(Integer episodeIndex) {
        this.episodeIndex = episodeIndex;
    }

    public String getDanmakuId() {
        return danmakuId;
    }

    public void setDanmakuId(String danmakuId) {
        this.danmakuId = danmakuId;
    }

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public String getBangumiName() {
        return bangumiName;
    }

    public void setBangumiName(String bangumiName) {
        this.bangumiName = bangumiName;
    }

    public Long getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Long bangumiId) {
        this.bangumiId = bangumiId;
    }

    public Byte getReplyable() {
        return replyable;
    }

    public void setReplyable(Byte replyable) {
        this.replyable = replyable;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getDanmakuCount() {
        return danmakuCount;
    }

    public void setDanmakuCount(Long danmakuCount) {
        this.danmakuCount = danmakuCount;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "danmakuId='" + danmakuId + '\'' +
                ", episodeId=" + episodeId +
                ", bangumiName='" + bangumiName + '\'' +
                ", bangumiId=" + bangumiId +
                ", episodeIndex=" + episodeIndex +
                ", replyable=" + replyable +
                '}';
    }
}
