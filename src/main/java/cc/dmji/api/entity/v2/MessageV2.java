package cc.dmji.api.entity.v2;

import cc.dmji.api.enums.MessageType;
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
    // 需要发通知的对象
    @Column(name = "uid")
    private Long uid;
    // 发送该通知的人
    @Column(name = "puid")
    private Long publisherUid;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "is_read", columnDefinition = "tinyint(1)")
    private boolean isRead;

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
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Long getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(Long publisherUid) {
        this.publisherUid = publisherUid;
    }

    @Override
    public String toString() {
        return "MessageV2{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", uid=" + uid +
                ", publisherUid=" + publisherUid +
                ", createTime=" + createTime +
                ", isRead=" + isRead +
                '}';
    }
}
