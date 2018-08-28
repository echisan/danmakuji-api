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
@Table(name = "dm_episode", schema = "dmji", catalog = "")
public class Episode {
    private Long epId;
    private Integer epIndex;
    private Long bangumiId;
    private Byte replyable;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private String danmakuId;
    @Column(name = "view_count")
    private Long viewCount;
    @Column(name = "url",columnDefinition = "text")
    private String url;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ep_id")
    public Long getEpId() {
        return epId;
    }

    public void setEpId(Long epId) {
        this.epId = epId;
    }

    @Basic
    @Column(name = "ep_index")
    public Integer getEpIndex() {
        return epIndex;
    }

    public void setEpIndex(Integer epIndex) {
        this.epIndex = epIndex;
    }

    @Basic
    @Column(name = "bangumi_id")
    public Long getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Long bangumiId) {
        this.bangumiId = bangumiId;
    }

    @Basic
    @Column(name = "replyable")
    public Byte getReplyable() {
        return replyable;
    }

    public void setReplyable(Byte replyable) {
        this.replyable = replyable;
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
    @Column(name = "danmaku_id")
    public String getDanmakuId() {
        return danmakuId;
    }

    public void setDanmakuId(String danmakuId) {
        this.danmakuId = danmakuId;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
