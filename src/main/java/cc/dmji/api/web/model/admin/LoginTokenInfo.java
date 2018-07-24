package cc.dmji.api.web.model.admin;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by echisan on 2018/6/9
 */
public class LoginTokenInfo implements Serializable {

    // 用户id
    private Long uid;
    // 用户名
    private String nick;
    // 颁发时间
    private Date issAt;
    // 过期时间
    private Date expAt;
    // token
    private String token;

    public LoginTokenInfo(Long uid, String nick, Date issAt, Date expAt, String token) {
        this.uid = uid;
        this.nick = nick;
        this.issAt = issAt;
        this.expAt = expAt;
        this.token = token;
    }

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

    public Date getIssAt() {
        return issAt;
    }

    public void setIssAt(Date issAt) {
        this.issAt = issAt;
    }

    public Date getExpAt() {
        return expAt;
    }

    public void setExpAt(Date expAt) {
        this.expAt = expAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
