package cc.dmji.api.web.model.v2.reply;

import cc.dmji.api.utils.PageInfo;

public class JumpSubPageInfo extends PageInfo {
    private Long rootId;

    public JumpSubPageInfo() {
    }

    public JumpSubPageInfo(Long rootId) {
        this.rootId = rootId;
    }

    public JumpSubPageInfo(int pageNumber, int pageSize, long totalSize, Long rootId) {
        super(pageNumber, pageSize, totalSize);
        this.rootId = rootId;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

}
