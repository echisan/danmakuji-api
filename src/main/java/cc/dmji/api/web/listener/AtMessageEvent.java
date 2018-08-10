package cc.dmji.api.web.listener;

import cc.dmji.api.entity.v2.ReplyV2;

import java.util.List;

public class AtMessageEvent extends ReplyMessageEvent {

    private List<String> nicks;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AtMessageEvent(Object source) {
        super(source);
    }

    public AtMessageEvent(Object source, Long uid, Long publisherUid, ReplyV2 replyV2, List<String> nicks) {
        super(source, uid, publisherUid, replyV2);
        this.nicks = nicks;
    }

    public List<String> getNicks() {
        return nicks;
    }

    public void setNicks(List<String> nicks) {
        this.nicks = nicks;
    }
}
