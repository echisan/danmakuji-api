package cc.dmji.api.service.impl;

import cc.dmji.api.entity.UserLogRecord;
import cc.dmji.api.enums.Role;
import cc.dmji.api.repository.UserLogRecordRepository;
import cc.dmji.api.service.UserLogRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by echisan on 2018/6/19
 */
@Service
public class UserLogRecordServiceImpl implements UserLogRecordService {
    private static final Logger logger = LoggerFactory.getLogger(UserLogRecordServiceImpl.class);

    @Autowired
    private UserLogRecordRepository userLogRecordRepository;

    @Override
    @Transactional
    public UserLogRecord insertUserLogRecord(UserLogRecord userLogRecord) {
        return userLogRecordRepository.save(userLogRecord);
    }

    @Override
    @Transactional
    public void deleteUserLogRecordById(Long id) {
        userLogRecordRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteUserLogRecordByIds(List<UserLogRecord> userLogRecords) {
        userLogRecordRepository.deleteInBatch(userLogRecords);
    }

    @Override
    @Transactional
    public UserLogRecord updateUserLogRecord(UserLogRecord userLogRecord) {
        return userLogRecordRepository.save(userLogRecord);
    }

    @Override
    public UserLogRecord getUserLogRecordById(Long id) {
        return userLogRecordRepository.getOne(id);
    }

    @Override
    public List<UserLogRecord> listUserLogRecords(Role role, Integer pn, Integer ps) {
        Integer limit = pn == 1 ? 0 : (pn - 1) * ps;
        return userLogRecordRepository.listUserLogRecords(role.getName(), limit, ps);
    }

    @Override
    public List<UserLogRecord> listUserLogRecords() {
        return userLogRecordRepository.findAll(Sort.by(Sort.Order.desc("create_time")));
    }

    @Override
    public List<UserLogRecord> insertUserLogRecords(List<UserLogRecord> userLogRecords) {
        return userLogRecordRepository.saveAll(userLogRecords);
    }
}
