package cc.dmji.api.web.model;

/**
 * Created by echisan on 2018/5/26
 */
public class UserInfo {

    // 用户id
    private String uid = "";

    // 用户名
    private String nick = "";

    // 用户头像
    private String face = "";

    // 用户性别
    private String sex = "";

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

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid='" + uid + '\'' +
                ", nick='" + nick + '\'' +
                ", face='" + face + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
