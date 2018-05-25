package cc.dmji.api.web.model;

public class VideoInfo {

    private String danmakuId;
    private Integer episodeId;
    private String bangumiName;
    private Integer bangumiId;
    private Integer episodeIndex;
    private Byte replyable;

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

    public Byte getReplyable() {
        return replyable;
    }

    public void setReplyable(Byte replyable) {
        this.replyable = replyable;
    }
}
