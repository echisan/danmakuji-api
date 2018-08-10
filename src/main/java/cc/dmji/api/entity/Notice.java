package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/22
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_notice", schema = "dmji", catalog = "")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "modify_time")
    private Timestamp modifyTime;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "view_count")
    private Long viewCount;

    /**
     * 是否显示在首页
     */
    @Column(name = "is_show_index")
    private Byte isShowIndex;

    public Long getId() {
        return id;
    }

    public Notice() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getIsShowIndex() {
        return isShowIndex;
    }

    public void setIsShowIndex(Byte isShowIndex) {
        this.isShowIndex = isShowIndex;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", userId='" + userId + '\'' +
                ", isShowIndex=" + isShowIndex +
                '}';
    }
}
