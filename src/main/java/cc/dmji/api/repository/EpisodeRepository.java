package cc.dmji.api.repository;

import cc.dmji.api.entity.Episode;
import cc.dmji.api.utils.EpisodePageInfo;
import cc.dmji.api.web.model.VideoInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode,Integer> {
    Episode findEpisodeByBangumiIdEqualsAndEpIndexEquals(Integer bangumiId, Integer epId);
    Page<Episode> findEpisodesByBangumiIdEquals(Integer bangumiId, Pageable pageable);
    List<Episode> findEpisodesByBangumiIdEquals(Integer bangumiId);
    long countEpisodesByBangumiIdEquals(int bangumiId);
}
