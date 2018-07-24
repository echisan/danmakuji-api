package cc.dmji.api.entity;

import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 用户提供的番剧信息
 * 包括审核的信息
 * Created by echisan on 2018/7/12
 */
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "dm_post_bangumi", schema = "dmji", catalog = "")
public class PostBangumi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 番剧名称
     */
    @Column(name = "bangumi_name")
    private String bangumiName;

    /**
     * 番剧总集数
     */
    @Column(name = "episode_total")
    private Integer episodeTotal;

    /**
     * 番剧封面
     */
    @Column(name = "thumb")
    private String thumb;

    /**
     * 是否有第0集
     */
    @Column(name = "has_zero_index")
    private Byte hasZeroIndex;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Timestamp modifyTime;

    /**
     * 该条记录的状态 NORMAL ,DELETE
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * 提交番剧信息的状态
     */
    @Column(name = "post_bangumi_status")
    @Enumerated(EnumType.STRING)
    private PostBangumiStatus postBangumiStatus;

    /**
     * 处理之后可能需要附加的信息
     */
    @Column(name = "message")
    private String message;

    /**
     * 提交番剧信息的用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 处理该请求的管理员id
     */
    @Column(name = "manager_user_id")
    private Long managerUserId;

    /**
     * 是否将个人信息展示到播放页面,默认显示
     */
    @Column(name = "is_show")
    private Byte isShow;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBangumiName() {
        return bangumiName;
    }

    public void setBangumiName(String bangumiName) {
        this.bangumiName = bangumiName;
    }

    public Integer getEpisodeTotal() {
        return episodeTotal;
    }

    public void setEpisodeTotal(Integer episodeTotal) {
        this.episodeTotal = episodeTotal;
    }

    public Byte getHasZeroIndex() {
        return hasZeroIndex;
    }

    public void setHasZeroIndex(Byte hasZeroIndex) {
        this.hasZeroIndex = hasZeroIndex;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PostBangumiStatus getPostBangumiStatus() {
        return postBangumiStatus;
    }

    public void setPostBangumiStatus(PostBangumiStatus postBangumiStatus) {
        this.postBangumiStatus = postBangumiStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(Long managerUserId) {
        this.managerUserId = managerUserId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Byte getIsShow() {
        return isShow;
    }

    public void setIsShow(Byte isShow) {
        this.isShow = isShow;
    }

    @Override
    public String toString() {
        return "PostBangumi{" +
                "id=" + id +
                ", bangumiName='" + bangumiName + '\'' +
                ", episodeTotal=" + episodeTotal +
                ", thumb='" + thumb + '\'' +
                ", hasZeroIndex=" + hasZeroIndex +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", status=" + status +
                ", postBangumiStatus=" + postBangumiStatus +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", managerUserId='" + managerUserId + '\'' +
                ", isShow=" + isShow +
                '}';
    }
}
