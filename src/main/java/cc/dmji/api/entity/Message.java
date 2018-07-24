package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_message", schema = "dmji", catalog = "")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    @Column
    private String type;
    @Column(name = "is_read")
    private Byte isRead;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "modify_time")
    private Timestamp modifyTime;
    @Column(name = "m_status")
    private String mStatus;
    @Column(name = "ep_id")
    private Long epId;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "publisher_uid")
    private Long publisherUserId;

    @OneToOne(targetEntity = Reply.class)
    @JoinColumn(name = "replyId")
    private Reply reply;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public Long getEpId() {
        return epId;
    }

    public void setEpId(Long epId) {
        this.epId = epId;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPublisherUserId() {
        return publisherUserId;
    }

    public void setPublisherUserId(Long publisherUserId) {
        this.publisherUserId = publisherUserId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                ", userId='" + userId + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", mStatus='" + mStatus + '\'' +
                ", epId=" + epId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", publisherUserId='" + publisherUserId + '\'' +
                ", reply=" + reply +
                '}';
    }
}
