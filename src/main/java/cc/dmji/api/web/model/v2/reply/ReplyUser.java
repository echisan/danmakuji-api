package cc.dmji.api.web.model.v2.reply;

/**
 * Created by echisan on 2018/7/26
 */
public class ReplyUser {
    private Long uid;
    private String nick;
    private String sign;
    private String avatar;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ReplyUser{" +
                "uid=" + uid +
                ", nick='" + nick + '\'' +
                ", sign='" + sign + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
