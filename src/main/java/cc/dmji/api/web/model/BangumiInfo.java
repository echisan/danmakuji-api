package cc.dmji.api.web.model;

import cc.dmji.api.entity.Bangumi;

import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/22
 */
public class BangumiInfo {
    private Long bangumiId;
    private String bangumiName;
    private Integer episodeTotal;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private String thumb;
    private Long viewCount;
    private Long danmaukuCount;

    public BangumiInfo() {
    }

    public BangumiInfo(Bangumi bangumi) {
        this.bangumiId = bangumi.getBangumiId();
        this.bangumiName = bangumi.getBangumiName();
        this.episodeTotal = bangumi.getEpisodeTotal();
        this.modifyTime = bangumi.getModifyTime();
        this.createTime = bangumi.getCreateTime();
        this.thumb = bangumi.getThumb();
        this.viewCount = bangumi.getViewCount();
    }

    public Long getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Long bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getBangumiName() {
        return bangumiName;
    }

    public void setBangumiName(String bangumiName) {
        this.bangumiName = bangumiName;
    }

    public Integer getEpisodeTotal() {
        return episodeTotal;
    }

    public void setEpisodeTotal(Integer episodeTotal) {
        this.episodeTotal = episodeTotal;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
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

    public Long getDanmaukuCount() {
        return danmaukuCount;
    }

    public void setDanmaukuCount(Long danmaukuCount) {
        this.danmaukuCount = danmaukuCount;
    }

    @Override
    public String toString() {
        return "BangumiInfo{" +
                "bangumiId=" + bangumiId +
                ", bangumiName='" + bangumiName + '\'' +
                ", episodeTotal=" + episodeTotal +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", thumb='" + thumb + '\'' +
                ", viewCount=" + viewCount +
                ", danmaukuCount=" + danmaukuCount +
                '}';
    }
}
