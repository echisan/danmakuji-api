package cc.dmji.api.repository;

import cc.dmji.api.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findByReportTargetTypeEquals(Integer reportTargetType, Pageable pageable);

    Report findByReportTargetTypeEqualsAndReportTargetIdAndPublisherUserIdEquals(Integer targetType, Long targetId, Long publisherId);

    Page<Report> findByReportHandleStatusEquals(Integer handleStatus,Pageable pageable);

    Report findByReportTargetTypeEqualsAndReportTargetId(Integer targetType, Long targetId);

    List<Report> findByReportTargetTypeEqualsAndReportTargetIdEquals(Integer targetType, Long targetId);
}
