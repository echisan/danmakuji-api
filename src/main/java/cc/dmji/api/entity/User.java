package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@DynamicInsert
@DynamicUpdate
@GenericGenerator(name = "jpa-uuid",strategy = "uuid")
@Table(name = "dm_user", schema = "dmji", catalog = "")
public class User {
    private String userId;
    private String email;
    private String pwd;
    private String phone;
    private String nick;
    private Integer age;
    private String sex;
    private String face;
    private String role;
    private Byte emailVerified;
    private Byte phoneVerified;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private Byte isLock;
    private Integer lockTime;

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "pwd")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "nick")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Basic
    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Basic
    @Column(name = "sex")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Basic
    @Column(name = "face")
    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "email_verified")
    public Byte getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Byte emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Basic
    @Column(name = "phone_verified")
    public Byte getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Byte phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "modify_time")
    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Basic
    @Column(name = "is_lock")
    public Byte getIsLock() {
        return isLock;
    }

    public void setIsLock(Byte isLock) {
        this.isLock = isLock;
    }

    @Basic
    @Column(name = "lock_time")
    public Integer getLockTime() {
        return lockTime;
    }

    public void setLockTime(Integer lockTime) {
        this.lockTime = lockTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                ", phone='" + phone + '\'' +
                ", nick='" + nick + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", face='" + face + '\'' +
                ", role='" + role + '\'' +
                ", emailVerified=" + emailVerified +
                ", phoneVerified=" + phoneVerified +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", isLock=" + isLock +
                ", lockTime=" + lockTime +
                '}';
    }
}
