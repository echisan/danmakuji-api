package cc.dmji.api.web.model;

import cc.dmji.api.entity.Reply;

/**
 * 这是一条评论里所需的内容
 * Created by echisan on 2018/5/26
 */
public class ReplyRequest {

    // 回复的用户的id
    private String uid;

    // 集数id
    private Integer ep_id;

    // 回复正文
    private String content;

    // 回复所在的页数
    private Integer r_page;

    // 是否是父级评论
    private Byte is_parent;

    // 父级评论的用户的id
    private String p_uid;

    // 父级评论id
    private String p_rid;

    public ReplyRequest() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getP_uid() {
        return p_uid;
    }

    public void setP_uid(String p_uid) {
        this.p_uid = p_uid;
    }

    public String getP_rid() {
        return p_rid;
    }

    public void setP_rid(String p_rid) {
        this.p_rid = p_rid;
    }

    public Integer getEp_id() {
        return ep_id;
    }

    public void setEp_id(Integer ep_id) {
        this.ep_id = ep_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getR_page() {
        return r_page;
    }

    public void setR_page(Integer r_page) {
        this.r_page = r_page;
    }

    public Byte getIs_parent() {
        return is_parent;
    }

    public void setIs_parent(Byte is_parent) {
        this.is_parent = is_parent;
    }
}
