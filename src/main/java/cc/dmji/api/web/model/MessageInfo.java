package cc.dmji.api.web.model;


import cc.dmji.api.entity.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by echisan on 2018/6/27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageInfo {

    private String id;

    private String type;
    @JsonProperty("is_read")
    private Byte isRead;
    @JsonProperty("u_id")
    private String userId;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("modify_time")
    private Date modifyTime;

    @JsonProperty("ep_id")
    private Integer epId;
    // 消息的内容
    @JsonProperty("content")
    private String content;
    @JsonProperty("title")
    private String title;

    @JsonProperty("reply_id")
    private String replyId;

    @JsonProperty("publisher")
    private UserInfo userInfo;

    public MessageInfo(Message message) {
        this.id = message.getId();
        this.type = message.getType();
        this.isRead = message.getIsRead();
        this.userId = message.getUserId();
        this.createTime = message.getCreateTime();
        this.modifyTime = message.getModifyTime();
        this.epId  = message.getEpId();
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                ", userId='" + userId + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", epId=" + epId +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", replyId='" + replyId + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
