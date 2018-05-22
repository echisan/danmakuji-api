package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_bangumi", schema = "dmji", catalog = "")
public class Bangumi {
    private Integer bangumiId;
    private String bangumiName;
    private Integer episodeTotal;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private String thumb;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bangumi_id")
    public Integer getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Integer bangumiId) {
        this.bangumiId = bangumiId;
    }

    @Basic
    @Column(name = "bangumi_name")
    public String getBangumiName() {
        return bangumiName;
    }

    public void setBangumiName(String bangumiName) {
        this.bangumiName = bangumiName;
    }

    @Basic
    @Column(name = "episode_total")
    public Integer getEpisodeTotal() {
        return episodeTotal;
    }

    public void setEpisodeTotal(Integer episodeTotal) {
        this.episodeTotal = episodeTotal;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "modify_time")
    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Basic
    @Column(name = "thumb")
    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public String toString() {
        return "Bangumi{" +
                "bangumiId=" + bangumiId +
                ", bangumiName='" + bangumiName + '\'' +
                ", episodeTotal=" + episodeTotal +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", thumb='" + thumb + '\'' +
                '}';
    }
}
