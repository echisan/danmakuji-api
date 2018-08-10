package cc.dmji.api.web.model.admin;

import cc.dmji.api.web.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/12
 */
public class PostBangumiInfo {
    private Long id;
    @JsonProperty("bn")
    private String bangumiName;
    @JsonProperty("et")
    private Integer episodeTotal;
    private String thumb;
    @JsonProperty("hzi")
    private Byte hasZeroIndex;
    @JsonProperty("create_time")
    private Timestamp createTime;
    @JsonProperty("modify_time")
    private Timestamp modifyTime;
    @JsonProperty("status")
    private String status;
    @JsonProperty("pbs")
    private String postBangumiStatus;
    @JsonProperty("pbs_name")
    private String postBangumiStatusName;
    @JsonProperty("msg")
    private String message;
    @JsonProperty("is_show")
    private Byte isShow = (byte)1;
    @JsonProperty("post_user")
    private UserInfo postUserInfo;
    @JsonProperty("manager_user")
    private UserInfo managerUserInfo;

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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostBangumiStatus() {
        return postBangumiStatus;
    }

    public void setPostBangumiStatus(String postBangumiStatus) {
        this.postBangumiStatus = postBangumiStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Byte getIsShow() {
        return isShow;
    }

    public void setIsShow(Byte isShow) {
        this.isShow = isShow;
    }

    public UserInfo getPostUserInfo() {
        return postUserInfo;
    }

    public void setPostUserInfo(UserInfo postUserInfo) {
        this.postUserInfo = postUserInfo;
    }

    public UserInfo getManagerUserInfo() {
        return managerUserInfo;
    }

    public void setManagerUserInfo(UserInfo managerUserInfo) {
        this.managerUserInfo = managerUserInfo;
    }

    public String getPostBangumiStatusName() {
        return postBangumiStatusName;
    }

    public void setPostBangumiStatusName(String postBangumiStatusName) {
        this.postBangumiStatusName = postBangumiStatusName;
    }

    @Override
    public String toString() {
        return "PostBangumiInfo{" +
                "id=" + id +
                ", bangumiName='" + bangumiName + '\'' +
                ", episodeTotal=" + episodeTotal +
                ", thumb='" + thumb + '\'' +
                ", hasZeroIndex=" + hasZeroIndex +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", status='" + status + '\'' +
                ", postBangumiStatus='" + postBangumiStatus + '\'' +
                ", message='" + message + '\'' +
                ", isShow=" + isShow +
                ", postUserInfo=" + postUserInfo +
                ", managerUserInfo=" + managerUserInfo +
                '}';
    }
}
