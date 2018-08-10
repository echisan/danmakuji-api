package cc.dmji.api.service.impl;

import cc.dmji.api.entity.OnlineRecord;
import cc.dmji.api.repository.OnlineRecordRepository;
import cc.dmji.api.service.OnlineRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by echisan on 2018/6/20
 */
@Service
public class OnlineRecordServiceImpl implements OnlineRecordService {
    private static final Logger logger = LoggerFactory.getLogger(OnlineRecordServiceImpl.class);

    @Autowired
    private OnlineRecordRepository onlineRecordRepository;

    @Override
    public OnlineRecord insert(OnlineRecord onlineRecord) {
        return onlineRecordRepository.save(onlineRecord);
    }

    @Override
    public List<OnlineRecord> insertRecords(List<OnlineRecord> onlineRecords) {
        return onlineRecordRepository.saveAll(onlineRecords);
    }

    @Override
    public void deleteOnlineRecordById(Long id) {
        onlineRecordRepository.deleteById(id);
    }

    @Override
    public void deleteOnlineRecords(List<OnlineRecord> onlineRecords) {
        onlineRecordRepository.deleteInBatch(onlineRecords);
    }

    @Override
    public OnlineRecord getById(Long id) {
        return onlineRecordRepository.getOne(id);
    }

    @Override
    public List<OnlineRecord> getAll() {
        return onlineRecordRepository.findAll();
    }

    @Override
    public List<OnlineRecord> getAll(Date begin, Date end) {
        return onlineRecordRepository.findOnlineRecordsByCreateTimeBetween(begin, end);
    }
}
