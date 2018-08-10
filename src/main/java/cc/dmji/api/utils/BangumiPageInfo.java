package cc.dmji.api.utils;

import cc.dmji.api.entity.Bangumi;

import java.util.ArrayList;
import java.util.List;

public class BangumiPageInfo {

    public static int DEFAULT_BANGUMI_PAGE_SIZE = 20;
    public static int DEFAULT_BANGUMI_PAGE_NUMBER = 1;
    private List<Bangumi> content;
    private PageInfo page;

    public BangumiPageInfo(){}

    public BangumiPageInfo(List<Bangumi> content, PageInfo page) {
        this.content = content;
        this.page = page;
    }

    public List<Bangumi> getContent() {
        return content;
    }

    public void setContent(List<Bangumi> content) {
        this.content = content;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }
}
