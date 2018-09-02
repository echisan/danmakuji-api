package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Report;
import cc.dmji.api.enums.ReportHandleStatus;
import cc.dmji.api.enums.ReportTargetType;
import cc.dmji.api.repository.ReportRepository;
import cc.dmji.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Report insert(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public Report update(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public Report getByid(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        reportRepository.deleteById(id);
    }

    @Override
    public Page<Report> listReport(Integer pn, Integer ps) {
        return reportRepository.findAll(PageRequest.of(pn - 1, ps, Sort.Direction.DESC, "createTime"));
    }

    @Override
    public Page<Report> listReportByReportTargetType(Integer pn, Integer ps, ReportTargetType reportTargetType) {
        return reportRepository.findByReportTargetTypeEquals(reportTargetType.getCode(),
                PageRequest.of(pn - 1, ps, Sort.Direction.DESC, "createTime"));
    }

    @Override
    public Report get(ReportTargetType reportTargetType, Long reportTargetId, Long publisherId) {
        return reportRepository.findByReportTargetTypeEqualsAndReportTargetIdAndPublisherUserIdEquals(
                reportTargetType.getCode(),reportTargetId,publisherId);
    }

    @Override
    public List<Report> listReportByTypeAndTargetId(ReportTargetType reportTargetType, Long targetId) {
        return reportRepository.findByReportTargetTypeEqualsAndReportTargetIdEquals(reportTargetType.getCode(),targetId);
    }

    @Override
    public List<Report> update(List<Report> reports) {
        return reportRepository.saveAll(reports);
    }

    @Override
    public Page<Report> listByHandleStatus(Integer pn, Integer ps, ReportHandleStatus reportHandleStatus) {
        return reportRepository.findByReportHandleStatusEquals(
                reportHandleStatus.getCode(),PageRequest.of(pn-1,ps, Sort.Direction.ASC,"createTime"));
    }
}
