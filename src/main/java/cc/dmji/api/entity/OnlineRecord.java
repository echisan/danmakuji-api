package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by echisan on 2018/6/20
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_online_record", schema = "dmji", catalog = "")
public class OnlineRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 最多在线人数 */
    @Column(name = "total")
    private Long total;

    /** 今日游客人数 */
    @Column(name = "anon")
    private Long anon;

    /** 今日注册人数 */
    @Column(name = "auth")
    private Long auth;

    /** api调用次数 */
    @Column(name = "api_count")
    private Long apiCount;

    /** 总共访问人数 */
    @Column(name = "total_visitors")
    private Long totalVisitors;

    /** 今日同时在线游客峰值 */
    private Long maxAnon;

    /** 今日同时在线用户峰值 */
    private Long maxAuth;

    /** 今日同时在线人数峰值 */
    private Long maxTotal;

    /** 创建时间 */
    @Column(name = "create_time")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getAnon() {
        return anon;
    }

    public void setAnon(Long anon) {
        this.anon = anon;
    }

    public Long getAuth() {
        return auth;
    }

    public void setAuth(Long auth) {
        this.auth = auth;
    }

    public Long getApiCount() {
        return apiCount;
    }

    public void setApiCount(Long apiCount) {
        this.apiCount = apiCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Long getTotalVisitors() {
        return totalVisitors;
    }

    public void setTotalVisitors(Long totalVisitors) {
        this.totalVisitors = totalVisitors;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getMaxAnon() {
        return maxAnon;
    }

    public void setMaxAnon(Long maxAnon) {
        this.maxAnon = maxAnon;
    }

    public Long getMaxAuth() {
        return maxAuth;
    }

    public void setMaxAuth(Long maxAuth) {
        this.maxAuth = maxAuth;
    }

    public Long getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Long maxTotal) {
        this.maxTotal = maxTotal;
    }

    @Override
    public String toString() {
        return "OnlineRecord{" +
                "id=" + id +
                ", total=" + total +
                ", anon=" + anon +
                ", auth=" + auth +
                ", apiCount=" + apiCount +
                ", totalVisitors=" + totalVisitors +
                ", maxAnon=" + maxAnon +
                ", maxAuth=" + maxAuth +
                ", maxTotal=" + maxTotal +
                ", createTime=" + createTime +
                '}';
    }
}
