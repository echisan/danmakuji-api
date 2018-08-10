package cc.dmji.api.web.model.v2.reply;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by echisan on 2018/7/26
 */
public class ReplyDetail {

    @JsonProperty("rpid")
    private Long id;
    // 子评论数量
    @JsonProperty("rcount")
    private Long replyCount = 0L;
    // 父级评论id
    private Long root;
    // 点赞数
    private Long like;
    // 点赞状态
    @JsonProperty("like_status")
    private Byte likeStatus;
    // 评论内容
    private String content;
    // 楼层
    private Long floor;
    // 状态
    private Byte status;
    // 创建时间
    @JsonProperty("ctime")
    private Timestamp createTime;
    // 子评论列表
    private List<ReplyDetail> replies;
    // 对象id
    private Long oid;
    // 回复的类型
    @JsonProperty("type")
    private Integer replyType;
    // 发送回复的用户信息
    private ReplyUser user;
    // 用户id
    private Long uid;

    public ReplyDetail() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getRoot() {
        return root;
    }

    public void setRoot(Long root) {
        this.root = root;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public List<ReplyDetail> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyDetail> replies) {
        this.replies = replies;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }

    public Integer getReplyType() {
        return replyType;
    }

    public void setReplyType(Integer replyType) {
        this.replyType = replyType;
    }

    public ReplyUser getUser() {
        return user;
    }

    public void setUser(ReplyUser user) {
        this.user = user;
    }

    public Long getFloor() {
        return floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(Byte likeStatus) {
        this.likeStatus = likeStatus;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "ReplyDetail{" +
                "id=" + id +
                ", replyCount=" + replyCount +
                ", root=" + root +
                ", like=" + like +
                ", likeStatus=" + likeStatus +
                ", content='" + content + '\'' +
                ", floor=" + floor +
                ", status=" + status +
                ", createTime=" + createTime +
                ", replies=" + replies +
                ", oid=" + oid +
                ", replyType=" + replyType +
                ", user=" + user +
                ", uid=" + uid +
                '}';
    }
}
