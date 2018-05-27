package cc.dmji.api.web.model;

import cc.dmji.api.utils.PageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by echisan on 2018/5/27
 */
public class Replies {

    private ReplyInfo reply;
    // 该评论下的子评论
    private List<ReplyInfo> replies;

    public Replies() {
        replies = new ArrayList<>();
    }

    public ReplyInfo getReply() {
        return reply;
    }

    public void setReply(ReplyInfo reply) {
        this.reply = reply;
    }

    public List<ReplyInfo> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyInfo> replies) {
        this.replies = replies;
    }
}
