package cc.dmji.api.web.model.v2.message;

import cc.dmji.api.web.model.UserInfo;

import java.sql.Timestamp;

public class MessageDetail {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    // 需要发通知的对象
    private Long uid;
    // 发送该通知的人
    private UserInfo publisher;
    private Timestamp createTime;

    public MessageDetail() {
    }

    public MessageDetail(Long id) {
        this.id = id;
    }

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

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public UserInfo getPublisher() {
        return publisher;
    }

    public void setPublisher(UserInfo publisher) {
        this.publisher = publisher;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "MessageDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", uid=" + uid +
                ", publisher=" + publisher +
                ", createTime=" + createTime +
                '}';
    }
}
