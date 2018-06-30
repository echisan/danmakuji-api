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
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "dm_message", schema = "dmji", catalog = "")
public class Message {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "message_id")
    private String id;
    @Column
    private String type;
    @Column(name = "is_read")
    private Byte isRead;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "modify_time")
    private Timestamp modifyTime;
    @Column(name = "m_status")
    private String mStatus;
    @Column(name = "ep_id")
    private Integer epId;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "publisher_uid")
    private String publisherUserId;

    @OneToOne(targetEntity = Reply.class)
    @JoinColumn(name = "replyId")
    private Reply reply;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
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

    public String getPublisherUserId() {
        return publisherUserId;
    }

    public void setPublisherUserId(String publisherUserId) {
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
