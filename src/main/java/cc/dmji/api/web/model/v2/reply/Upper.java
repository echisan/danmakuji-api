package cc.dmji.api.web.model.v2.reply;

/**
 * 置顶的评论
 * Created by echisan on 2018/7/26
 */
public class Upper {
    private Long uid;
    private ReplyDetail top;

    public Upper() {
    }

    public Upper(Long uid, ReplyDetail top) {
        this.uid = uid;
        this.top = top;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public ReplyDetail getTop() {
        return top;
    }

    public void setTop(ReplyDetail top) {
        this.top = top;
    }
}
