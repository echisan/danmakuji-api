package cc.dmji.api.service;

import cc.dmji.api.entity.Episode;
import cc.dmji.api.utils.EpisodePageInfo;
import cc.dmji.api.web.model.VideoInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EpisodeService {

    EpisodePageInfo listEpisodes();

    EpisodePageInfo listEpisodes(Integer pageNum);

    EpisodePageInfo listEpisodes(Integer pageNum, Integer pageSize);

    EpisodePageInfo listEpisodesByBangumiId(Integer bangumiId);

    EpisodePageInfo listEpisodesByBangumiId(Integer bangumiId, int pn);

    EpisodePageInfo listEpisodesByBangumiId(Integer bangumiId, int pn, int ps);

    List<Episode> listAllEpisodesByBangumiId(Integer bangumiId);

    List<Episode> listEpisodesByEpIds(List<Integer> epIds);

    Episode getEpisodeByBangumiIdAndEpIndex(Integer bangumiId, Integer epIndex);

    Episode getEpisodeByEpId(Integer epId);

    Episode insertEpisode(Episode episode);

    List<Episode> insertEpisodes(List<Episode> episodes);

    Episode updateEpisode(Episode episode);

    void deleteEpisode(Integer id);

    void deleteEpisodes(List<Episode> episodes);

    long countEpisode();

    long countEpisodeByBangumiId(int bangumiId);

}
