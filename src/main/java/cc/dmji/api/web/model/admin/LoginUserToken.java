package cc.dmji.api.web.model.admin;

import java.util.Date;

/**
 * Created by echisan on 2018/6/11
 */
public class LoginUserToken {

    private String key;
    private String token;
    private Date expireAt;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    @Override
    public String toString() {
        return "LoginUserToken{" +
                "key='" + key + '\'' +
                ", token='" + token + '\'' +
                ", expireAt=" + expireAt +
                '}';
    }
}
