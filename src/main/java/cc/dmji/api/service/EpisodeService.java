package cc.dmji.api.service;

import cc.dmji.api.entity.Episode;

import java.util.List;

public interface EpisodeService {

    List<Episode> listEpisodes();

    List<Episode> listEpisodesByBangumiId(Integer bangumiId);

    Episode getEpisodeByBangumiIdAndEpIndex(Integer bangumiId, Integer epIndex);

    Episode getEpisodeByEpId(Integer epId);

    Episode insertEpisode(Episode episode);

    Episode updateEpisode(Episode episode);

    void deleteEpisode(Integer id);

    Long countEpisode();

}
