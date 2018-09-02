package cc.dmji.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 弹幕实体
 */
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "dm_danmaku")
public class Danmaku implements Serializable {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * 弹幕发送时间
     */
    @Column(name = "time")
    private double time;
    /**
     * 弹幕正文
     */
    @Column(name = "text")
    private String text;
    /**
     * 弹幕颜色
     */
    @Column(name = "color")
    private Integer color;
    /**
     * 弹幕类型（居中，顶部，滚动等）
     */
    @Column(name = "type")
    private Integer type;
    /**
     * 发送弹幕的ip地址
     */
    @JsonIgnore
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * 发送弹幕的用户
     */
    @Column(name = "username")
    private String username;

    /**
     * 弹幕池id
     */
    @Column(name = "danmaku_id")
    private String danmakuId;

    /**
     * 请求的域名
     */
    @JsonIgnore
    @Column(name = "referer")
    private String referer;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 发送的时间
     */
    @Column(name = "create_time")
    private Timestamp createTime;

    public Danmaku() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDanmakuId() {
        return danmakuId;
    }

    public void setDanmakuId(String danmakuId) {
        this.danmakuId = danmakuId;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
