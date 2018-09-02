package cc.dmji.api.service;

import cc.dmji.api.entity.Report;
import cc.dmji.api.enums.ReportHandleStatus;
import cc.dmji.api.enums.ReportTargetType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService {

    Report insert(Report report);

    Report update(Report report);

    Report getByid(Long id);

    void deleteById(Long id);

    Page<Report> listReport(Integer pn, Integer ps);

    Page<Report> listReportByReportTargetType(Integer pn, Integer ps, ReportTargetType reportTargetType);

    Page<Report> listByHandleStatus(Integer pn, Integer ps, ReportHandleStatus reportHandleStatus);

    Report get(ReportTargetType reportTargetType, Long reportTargetId, Long publisherId);

    List<Report> listReportByTypeAndTargetId(ReportTargetType reportTargetType, Long targetId);

    List<Report> update(List<Report> reports);
}
