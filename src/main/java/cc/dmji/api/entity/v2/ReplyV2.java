package cc.dmji.api.entity.v2;

import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.ReplyType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/26
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_reply_v2", schema = "dmji")
public class ReplyV2 {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reply_status")
    @Enumerated(value = EnumType.ORDINAL)
    private Status status;

    @Column(name = "root")
    private Long root;

    @Column(name = "floor")
    private Long floor;

    @Column(name = "reply_type")
    private Integer replyType;

    /**
     * 对象id，与replyType配合使用
     * 假如是某番剧某集下的评论，则
     * object_id = epId and type = ReplyType.BANGUMI_EPISODE
     */
    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "is_top", columnDefinition = "tinyint(1)")
    private boolean top;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setReplyType(Integer replyType) {
        this.replyType = replyType;
    }

    public Long getRoot() {
        return root;
    }

    public void setRoot(Long root) {
        this.root = root;
    }

    public Long getFloor() {
        return floor;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public ReplyType getReplyType() {
        return ReplyType.byCode(replyType);
    }

    public void setReplyType(ReplyType replyType) {
        this.replyType = replyType.getCode();
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }
}
