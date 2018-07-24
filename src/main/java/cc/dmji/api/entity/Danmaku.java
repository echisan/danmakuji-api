package cc.dmji.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

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
    private String color;
    /**
     * 弹幕类型（居中，顶部，滚动等）
     */
    @Column(name = "type")
    private String type;
    /**
     * 发送弹幕的ip地址
     */
    @JsonIgnore
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * 发送弹幕的用户
     */
    @Column(name = "author")
    private String author;

    /**
     * 弹幕池id
     */
    @Column(name = "player")
    private String player;

    /**
     * 请求的域名
     */
    @JsonIgnore
    @Column(name = "referer")
    private String referer;

    @Column(name = "user_id")
    private Long userId;

    @Transient
    private String token;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "DanmakuEntity{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", text='" + text + '\'' +
                ", color='" + color + '\'' +
                ", type='" + type + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", author='" + author + '\'' +
                ", player='" + player + '\'' +
                '}';
    }
}
