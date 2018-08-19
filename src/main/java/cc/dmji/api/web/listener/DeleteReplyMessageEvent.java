package cc.dmji.api.web.listener;

import cc.dmji.api.entity.v2.ReplyV2;
import org.springframework.context.ApplicationEvent;

public class DeleteReplyMessageEvent extends ApplicationEvent {
    private Long targetUid;
    private Long publisherUid;
    private ReplyV2 deleteReply;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DeleteReplyMessageEvent(Object source) {
        super(source);
    }

    public DeleteReplyMessageEvent(Object source, Long targetUid, Long publisherUid, ReplyV2 deleteReply) {
        super(source);
        this.targetUid = targetUid;
        this.publisherUid = publisherUid;
        this.deleteReply = deleteReply;
    }

    public Long getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(Long targetUid) {
        this.targetUid = targetUid;
    }

    public Long getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(Long publisherUid) {
        this.publisherUid = publisherUid;
    }

    public ReplyV2 getDeleteReply() {
        return deleteReply;
    }

    public void setDeleteReply(ReplyV2 deleteReply) {
        this.deleteReply = deleteReply;
    }
}
