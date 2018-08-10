package cc.dmji.api.web.model;

import cc.dmji.api.entity.Reply;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by echisan on 2018/5/26
 */
public class ReplyInfo {

    private UserInfo user;
    private Reply reply;
    private Long totalSize = 0L;
    private Byte likeStatus = 0;
    @JsonProperty("cur_page")
    private Integer curPage = 1;
    @JsonProperty("is_target")
    private Integer isTarget = 0;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Byte getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(Byte likeStatus) {
        this.likeStatus = likeStatus;
    }

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getIsTarget() {
        return isTarget;
    }

    public void setIsTarget(Integer isTarget) {
        this.isTarget = isTarget;
    }

    @Override
    public String toString() {
        return "ReplyInfo{" +
                "user=" + user +
                ", reply=" + reply +
                ", totalSize=" + totalSize +
                ", likeStatus=" + likeStatus +
                '}';
    }
}
