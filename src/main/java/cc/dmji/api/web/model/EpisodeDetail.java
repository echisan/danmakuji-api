package cc.dmji.api.web.model;

public class EpisodeDetail {
    private Long epId;
    private Long bangumiId;
    private Long danmakuCount;
    private Integer epIndex;
    private Long episodeViewCount;
    private Long bangumiViewCount;
    private String bangumiName;
    private String thumb;
    private Integer episodeTotal;
    private String danmakuId;

    public Long getEpId() {
        return epId;
    }

    public void setEpId(Long epId) {
        this.epId = epId;
    }

    public Long getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Long bangumiId) {
        this.bangumiId = bangumiId;
    }

    public Long getDanmakuCount() {
        return danmakuCount;
    }

    public void setDanmakuCount(Long danmakuCount) {
        this.danmakuCount = danmakuCount;
    }

    public Integer getEpIndex() {
        return epIndex;
    }

    public void setEpIndex(Integer epIndex) {
        this.epIndex = epIndex;
    }

    public Long getEpisodeViewCount() {
        return episodeViewCount;
    }

    public void setEpisodeViewCount(Long episodeViewCount) {
        this.episodeViewCount = episodeViewCount;
    }

    public Long getBangumiViewCount() {
        return bangumiViewCount;
    }

    public void setBangumiViewCount(Long bangumiViewCount) {
        this.bangumiViewCount = bangumiViewCount;
    }

    public String getBangumiName() {
        return bangumiName;
    }

    public void setBangumiName(String bangumiName) {
        this.bangumiName = bangumiName;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getEpisodeTotal() {
        return episodeTotal;
    }

    public void setEpisodeTotal(Integer episodeTotal) {
        this.episodeTotal = episodeTotal;
    }

    public String getDanmakuId() {
        return danmakuId;
    }

    public void setDanmakuId(String danmakuId) {
        this.danmakuId = danmakuId;
    }
}
