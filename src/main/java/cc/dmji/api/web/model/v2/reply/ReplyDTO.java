package cc.dmji.api.web.model.v2.reply;

import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.ReplyType;

/**
 * Created by echisan on 2018/7/26
 */
public class ReplyDTO {

    private Long userId;
    private ReplyType replyType;
    private Long objectId;
    private String content;
    private Long root;
    private Long floor;

    public ReplyDTO() {
    }

    public ReplyDTO(Long userId, ReplyType replyType, Long objectId, String content, Long root, Long floor) {
        this.userId = userId;
        this.replyType = replyType;
        this.objectId = objectId;
        this.content = content;
        this.root = root;
        this.floor = floor;
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
        return replyType;
    }

    public void setReplyType(ReplyType replyType) {
        this.replyType = replyType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
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


}
