package cc.dmji.api.repository;

import cc.dmji.api.entity.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    Episode findEpisodeByBangumiIdEqualsAndEpIndexEquals(Long bangumiId, Integer epIndex);

    Page<Episode> findEpisodesByBangumiIdEquals(Long bangumiId, Pageable pageable);

    List<Episode> findEpisodesByBangumiIdEquals(Long bangumiId);

    long countEpisodesByBangumiIdEquals(Long bangumiId);
}
