package cc.dmji.api.entity;

import cc.dmji.api.enums.Status;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 首页推荐的实体
 */
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "dm_index_recommend",schema = "dmji")
public class IndexRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "link_url",columnDefinition = "text")
    private String linkUrl;
    @Column(name = "image_url",columnDefinition = "text")
    private String imageUrl;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "modify_time")
    private Timestamp modifyTime;
    @Column(name = "recommend_status")
    @Enumerated(value = EnumType.ORDINAL)
    private Status recommendStatus;
    @Column(name = "publisher_id")
    private Long publisherId;
    @Column(name = "show_index",columnDefinition = "tinyint(1)")
    private boolean showIndex;

    public Long getId() {
        return id;
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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Status getRecommendStatus() {
        return recommendStatus;
    }

    public void setRecommendStatus(Status recommendStatus) {
        this.recommendStatus = recommendStatus;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public boolean isShowIndex() {
        return showIndex;
    }

    public void setShowIndex(boolean showIndex) {
        this.showIndex = showIndex;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "IndexRecommend{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", recommendStatus=" + recommendStatus +
                ", publisherId=" + publisherId +
                ", showIndex=" + showIndex +
                '}';
    }
}
