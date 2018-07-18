package cc.dmji.api.web.model;

import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.PostBangumiStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/17
 */
public class UserPostBangumi {
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
    @JsonProperty("pbs")
    private String postBangumiStatus;
    @JsonProperty("pbs_name")
    private String postBangumiStatusName;
    @JsonProperty("msg")
    private String message;
    @JsonProperty("is_show")
    private Byte isShow = (byte)1;

    public UserPostBangumi() {
    }

    public UserPostBangumi(PostBangumi pb) {
        this.id = pb.getId();
        this.bangumiName = pb.getBangumiName();
        this.episodeTotal = pb.getEpisodeTotal();
        this.thumb = pb.getThumb();
        this.hasZeroIndex = pb.getHasZeroIndex();
        this.createTime = pb.getCreateTime();
        this.modifyTime = pb.getModifyTime();
        this.postBangumiStatus = pb.getPostBangumiStatus().name();
        this.postBangumiStatusName = pb.getPostBangumiStatus().getStatusName();
        this.message = pb.getMessage();
        this.isShow = pb.getIsShow();
    }

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

    public String getPostBangumiStatus() {
        return postBangumiStatus;
    }

    public void setPostBangumiStatus(String postBangumiStatus) {
        this.postBangumiStatus = postBangumiStatus;
    }

    public String getPostBangumiStatusName() {
        return postBangumiStatusName;
    }

    public void setPostBangumiStatusName(String postBangumiStatusName) {
        this.postBangumiStatusName = postBangumiStatusName;
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
}
