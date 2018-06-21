package cc.dmji.api.utils;

import cc.dmji.api.entity.Episode;

import java.util.List;

public class EpisodePageInfo {

    private List<Episode> episodes;
    private PageInfo page;

    public EpisodePageInfo(){}

    public EpisodePageInfo(List<Episode> episodes, PageInfo page) {
        this.episodes = episodes;
        this.page = page;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }
}
