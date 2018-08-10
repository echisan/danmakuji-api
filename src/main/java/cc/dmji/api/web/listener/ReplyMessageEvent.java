package cc.dmji.api.web.listener;

import cc.dmji.api.entity.v2.ReplyV2;
import org.springframework.context.ApplicationEvent;

public class ReplyMessageEvent extends ApplicationEvent {
    private Long targetUid;
    private Long publisherUid;
    private ReplyV2 replyV2;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ReplyMessageEvent(Object source) {
        super(source);
    }

    public ReplyMessageEvent(Object source, Long uid, Long publisherUid, ReplyV2 replyV2) {
        super(source);
        this.targetUid = uid;
        this.publisherUid = publisherUid;
        this.replyV2 = replyV2;
    }

    public Long getUid() {
        return targetUid;
    }

    public void setUid(Long uid) {
        this.targetUid = uid;
    }

    public Long getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(Long publisherUid) {
        this.publisherUid = publisherUid;
    }

    public ReplyV2 getReplyV2() {
        return replyV2;
    }

    public void setReplyV2(ReplyV2 replyV2) {
        this.replyV2 = replyV2;
    }
}
