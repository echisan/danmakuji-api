package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by echisan on 2018/6/18
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_user_log_record",schema = "dmji", catalog = "")
public class UserLogRecord implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 客户端id
     */
    @Column(name = "client_id")
    private String clientId;

    /**
     * id地址
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * 用户角色
     */
    @Column(name = "user_role")
    private String userRole;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 请求uri
     */
    @Column(name = "uri")
    private String uri;

    /**
     * 请求地址
     */
    @Column(name = "url")
    private String url;

    /**
     * 请求方法
     */
    @Column(name = "method")
    private String method;

    /**
     * http方法 get/post/put/delete
     */
    @Column(name = "http_method")
    private String httpMethod;

    /**
     * 操作描述
     */
    @Column(name = "description")
    private String description;
    /**
     * 请求参数
     */
    @Column(name = "params")
    private String params;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @Column(name = "nick")
    private String nick;

    /**
     * 请求域
     */
    @Column(name = "referer")
    private String referer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "UserLogRecord{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", userRole='" + userRole + '\'' +
                ", createTime=" + createTime +
                ", uri='" + uri + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", description='" + description + '\'' +
                ", params='" + params + '\'' +
                ", userId='" + userId + '\'' +
                ", nick='" + nick + '\'' +
                ", referer='" + referer + '\'' +
                '}';
    }
}
