package cc.dmji.api.web.model.admin;

/**
 * 记录当前已登录的token信息
 * Created by echisan on 2018/6/11
 */
public class TokenRecord {

    private String uid;
    private String nick;
    private String token;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
