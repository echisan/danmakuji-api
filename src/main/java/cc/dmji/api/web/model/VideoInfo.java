package cc.dmji.api.web.model;

public class VideoInfo {

    String danmakuId;
    Integer episodeId;
    String bangumiName;
    Integer bangumiId;
    Integer episodeIndex;

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

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public String getBangumiName() {
        return bangumiName;
    }

    public void setBangumiName(String bangumiName) {
        this.bangumiName = bangumiName;
    }

    public Integer getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Integer bangumiId) {
        this.bangumiId = bangumiId;
    }
}
