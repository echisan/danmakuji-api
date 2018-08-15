package cc.dmji.api.entity.v2;

import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Status;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/27
 */
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "dm_message_v2", schema = "dmji")
public class MessageV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "type")
    private Integer type;
    @Column(name = "uid")
    private Long uid;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "is_read", columnDefinition = "tinyint(1)")
    private boolean read;
    @Column(name = "sys_msg_id")
    private Long sysMessageId;
    @Column(name = "puid")
    private Long publisherUid;
    @Column(name = "status")
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type.getCode();
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getSysMessageId() {
        return sysMessageId;
    }

    public void setSysMessageId(Long sysMessageId) {
        this.sysMessageId = sysMessageId;
    }

    public Long getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(Long publisherUid) {
        this.publisherUid = publisherUid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MessageV2{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", uid=" + uid +
                ", createTime=" + createTime +
                ", read=" + read +
                ", sysMessageId=" + sysMessageId +
                ", publisherUid=" + publisherUid +
                ", status=" + status +
                '}';
    }
}
