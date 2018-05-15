package cc.dmji.api.repository;

import cc.dmji.api.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode,Integer> {
    Episode findEpisodeByBangumiIdEqualsAndEpIndexEquals(Integer bangumiId, Integer epId);
    List<Episode> findEpisodesByBangumiIdEquals(Integer bangumiId);
}
