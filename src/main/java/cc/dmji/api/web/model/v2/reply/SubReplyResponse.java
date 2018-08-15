package cc.dmji.api.web.model.v2.reply;

import cc.dmji.api.utils.PageInfo;

import java.util.List;

public class SubReplyResponse {
    private List<ReplyDetail> replies;
    private ReplyDetail root;
    private PageInfo page;

    public List<ReplyDetail> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyDetail> replies) {
        this.replies = replies;
    }

    public ReplyDetail getRoot() {
        return root;
    }

    public void setRoot(ReplyDetail root) {
        this.root = root;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }
}
