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

    /** 在线人数 */
    @Column(name = "totle")
    private Long totle;

    /** 在线游客人数 */
    @Column(name = "anon")
    private Long anon;

    /** 在线注册人数 */
    @Column(name = "auth")
    private Long auth;

    /** 访问次数 */
    @Column(name = "visit_count")
    private Long visitCount;

    /** 创建时间 */
    @Column(name = "create_time")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotle() {
        return totle;
    }

    public void setTotle(Long totle) {
        this.totle = totle;
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

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "OnlineUserRecord{" +
                "id=" + id +
                ", totle=" + totle +
                ", anon=" + anon +
                ", auth=" + auth +
                ", visitCount=" + visitCount +
                ", createTime=" + createTime +
                '}';
    }
}
