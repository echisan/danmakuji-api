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
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "dm_message", schema = "dmji", catalog = "")
public class Message {
    private String messageId;
    private String type;
    private String replyId;
    private Byte isRead;
    private String userId;
    private String atAnchor;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private String mStatus;
    private Integer epId;

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "message_id")
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "reply_id")
    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    @Basic
    @Column(name = "is_read")
    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    @Basic
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "at_anchor")
    public String getAtAnchor() {
        return atAnchor;
    }

    public void setAtAnchor(String atAnchor) {
        this.atAnchor = atAnchor;
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
    @Column(name = "m_status")
    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    @Basic
    @Column(name = "ep_id")
    public Integer getEpId() {
        return epId;
    }

    public void setEpId(Integer epId) {
        this.epId = epId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", type='" + type + '\'' +
                ", replyId='" + replyId + '\'' +
                ", isRead=" + isRead +
                ", userId='" + userId + '\'' +
                ", atAnchor='" + atAnchor + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", mStatus='" + mStatus + '\'' +
                ", epId=" + epId +
                '}';
    }
}
