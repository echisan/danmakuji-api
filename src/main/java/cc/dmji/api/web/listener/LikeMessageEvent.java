package cc.dmji.api.web.listener;

import cc.dmji.api.entity.v2.ReplyV2;

public class LikeMessageEvent extends ReplyMessageEvent {
    public LikeMessageEvent(Object source, Long uid, Long publisherUid, ReplyV2 replyV2) {
        super(source, uid, publisherUid, replyV2);
    }
}
