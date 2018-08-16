package cc.dmji.api.utils;

import cc.dmji.api.enums.Role;

import java.util.Date;

/**
 * 基本包含了jwt payload部分的信息
 */
public class JwtUserInfo {
    private Long uid;
    private String nick;
    private Date createTime;
    private boolean emailVerify;
    private boolean lock;
    private Date issAt;
    private Date expiration;
    private Role role;

    public JwtUserInfo() {
    }

    public JwtUserInfo(Long uid, String nick,
                       Date createTime, boolean emailVerify,
                       boolean lock, Date issAt,
                       Date expiration, Role role) {

        this.uid = uid;
        this.nick = nick;
        this.createTime = createTime;
        this.emailVerify = emailVerify;
        this.lock = lock;
        this.issAt = issAt;
        this.expiration = expiration;
        this.role = role;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isEmailVerify() {
        return emailVerify;
    }

    public void setEmailVerify(boolean emailVerify) {
        this.emailVerify = emailVerify;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getIssAt() {
        return issAt;
    }

    public void setIssAt(Date issAt) {
        this.issAt = issAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "JwtUserInfo{" +
                "uid=" + uid +
                ", nick='" + nick + '\'' +
                ", createTime=" + createTime +
                ", emailVerify=" + emailVerify +
                ", lock=" + lock +
                ", issAt=" + issAt +
                ", expiration=" + expiration +
                ", role=" + role +
                '}';
    }
}
