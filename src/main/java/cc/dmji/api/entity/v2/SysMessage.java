package cc.dmji.api.entity.v2;

import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.SysMsgTargetType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_sys_message", schema = "dmji")
public class SysMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "status")
    private Status status;

    @Column(name = "puid")
    private Long publisherUid;

    @Column(name = "sys_msg_target_type")
    private Integer sysMsgTargetType;

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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(Long publisherUid) {
        this.publisherUid = publisherUid;
    }

    public SysMsgTargetType getSysMsgTargetType() {
        return SysMsgTargetType.byCode(sysMsgTargetType);
    }

    public void setSysMsgTargetType(Integer sysMsgTargetType) {
        this.sysMsgTargetType = sysMsgTargetType;
    }

    @Override
    public String toString() {
        return "SysMessage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                ", publisherUid=" + publisherUid +
                ", sysMsgTargetType=" + sysMsgTargetType +
                '}';
    }
}
