package cc.dmji.api.web.model.v2.reply;

import cc.dmji.api.utils.PageInfo;

import java.util.List;

/**
 * Created by echisan on 2018/7/27
 */
public class ReplyResponse {

    private List<ReplyDetail> replies;
    private List<ReplyDetail> hot;
    private ReplyDetail top;
    private PageInfo page;

    public List<ReplyDetail> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyDetail> replies) {
        this.replies = replies;
    }

    public List<ReplyDetail> getHot() {
        return hot;
    }

    public void setHot(List<ReplyDetail> hot) {
        this.hot = hot;
    }

    public ReplyDetail getTop() {
        return top;
    }

    public void setTop(ReplyDetail top) {
        this.top = top;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }
}
