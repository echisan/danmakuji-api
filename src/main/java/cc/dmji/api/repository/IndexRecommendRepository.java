package cc.dmji.api.repository;

import cc.dmji.api.entity.IndexRecommend;
import cc.dmji.api.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexRecommendRepository extends JpaRepository<IndexRecommend, Long> {
    Page<IndexRecommend> findByShowIndexEqualsAndRecommendStatusEquals(boolean showIndex, Status status, Pageable pageable);
}
