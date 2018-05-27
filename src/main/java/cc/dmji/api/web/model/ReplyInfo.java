package cc.dmji.api.web.model;

import cc.dmji.api.entity.Reply;

import java.util.List;

/**
 * Created by echisan on 2018/5/26
 */
public class ReplyInfo {

    private UserInfo user;
    private Reply reply;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "ReplyInfo{" +
                "user=" + user +
                ", reply=" + reply +
                '}';
    }
}
