package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_reply", schema = "dmji", catalog = "")
public class Reply {
    // 回复id
    private Long replyId;
    // 回复内容
    private String content;
    // 回复所在的页面
    private Integer rPage;
    private Timestamp createTime;
    private Timestamp modifyTime;
    // 回复的用户
    private Long userId;
    private String rStatus;
    private Long parentId;
    private Byte isParent;
    private Integer rLike;
    private Integer rHate;
    private Long epId;
    @Column(name = "floor")
    private Long floor;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    @Basic
    @Column(name = "content",columnDefinition = "text")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "r_page")
    public Integer getrPage() {
        return rPage;
    }

    public void setrPage(Integer rPage) {
        this.rPage = rPage;
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
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "r_status")
    public String getrStatus() {
        return rStatus;
    }

    public void setrStatus(String rStatus) {
        this.rStatus = rStatus;
    }

    @Basic
    @Column(name = "parent_id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "is_parent")
    public Byte getIsParent() {
        return isParent;
    }

    public void setIsParent(Byte isParent) {
        this.isParent = isParent;
    }

    @Basic
    @Column(name = "r_like")
    public Integer getrLike() {
        return rLike;
    }

    public void setrLike(Integer rLike) {
        this.rLike = rLike;
    }

    @Basic
    @Column(name = "r_hate")
    public Integer getrHate() {
        return rHate;
    }

    public void setrHate(Integer rHate) {
        this.rHate = rHate;
    }

    @Basic
    @Column(name = "ep_id")
    public Long getEpId() {
        return epId;
    }

    public void setEpId(Long epId) {
        this.epId = epId;
    }

    public Long getFloor() {
        return floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "ReplyV2{" +
                "replyId='" + replyId + '\'' +
                ", content='" + content + '\'' +
                ", rPage=" + rPage +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", userId='" + userId + '\'' +
                ", rStatus='" + rStatus + '\'' +
                ", parentId='" + parentId + '\'' +
                ", isParent=" + isParent +
                ", rLike=" + rLike +
                ", rHate=" + rHate +
                ", epId=" + epId +
                ", floor=" + floor +
                '}';
    }
}
