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

    EpisodePageInfo listEpisodesByBangumiId(Long bangumiId);

    EpisodePageInfo listEpisodesByBangumiId(Long bangumiId, int pn);

    EpisodePageInfo listEpisodesByBangumiId(Long bangumiId, int pn, int ps);

    List<Episode> listAllEpisodesByBangumiId(Long bangumiId);

    List<Episode> listEpisodesByEpIds(List<Long> epIds);

    Episode getEpisodeByBangumiIdAndEpIndex(Long bangumiId, Integer epIndex);

    Episode getEpisodeByEpId(Long epId);

    Episode insertEpisode(Episode episode);

    List<Episode> insertEpisodes(List<Episode> episodes);

    Episode updateEpisode(Episode episode);

    void deleteEpisode(Long id);

    void deleteEpisodes(List<Episode> episodes);

    long countEpisode();

    long countEpisodeByBangumiId(Long bangumiId);

    Page<Episode> listEpisodeByViewCount(Integer pn, Integer ps);

}
