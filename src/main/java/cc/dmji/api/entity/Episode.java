package cc.dmji.api.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@Table(name = "dm_episode", schema = "dmji", catalog = "")
public class Episode {
    private int epId;
    private Integer epIndex;
    private Byte replyable;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private String danmakuId;

    @Id
    @Column(name = "ep_id")
    public int getEpId() {
        return epId;
    }

    public void setEpId(int epId) {
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

}
