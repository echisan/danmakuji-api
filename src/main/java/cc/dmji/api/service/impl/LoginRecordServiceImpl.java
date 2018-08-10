package cc.dmji.api.service.impl;

import cc.dmji.api.entity.LoginRecord;
import cc.dmji.api.repository.LoginRecordRepository;
import cc.dmji.api.service.LoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class LoginRecordServiceImpl implements LoginRecordService {

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Override
    public List<LoginRecord> listLoginRecordsByUserId(Long userId) {
        return loginRecordRepository.findLoginRecordsByUserIdEquals(userId);
    }

    @Override
    public List<LoginRecord> listLoginRecordsByIp(String ip) {
        return loginRecordRepository.findLoginRecordsByIpEquals(ip);
    }

    @Override
    public List<LoginRecord> listLoginRecordsByUserIdAndIp(Long userId, String ip) {
        List<LoginRecord> result = null;
        result = loginRecordRepository.findLoginRecordsByUserIdEqualsAndIpEquals(userId,ip);
        return result;
    }

    @Override
    public List<LoginRecord> listLoginRecords() {
        return loginRecordRepository.findAll();
    }

    @Override
    public LoginRecord getLoginRecordByRecordId(Long recordId) {
        return loginRecordRepository.findById(recordId).orElse(null);
    }

    @Override
    public LoginRecord insertLoginRecord(LoginRecord loginRecord) {
        setCreateAndModifyTime(loginRecord);
        return loginRecordRepository.save(loginRecord);
    }

    @Override
    public LoginRecord updateLoginRecord(LoginRecord loginRecord) {
        setModifyTime(loginRecord);
        return loginRecordRepository.save(loginRecord);
    }

    @Override
    public void deleteLoginRecordByRecordId(Long recordId) {
        loginRecordRepository.deleteById(recordId);
    }

    @Override
    public void deleteLoginRecords(List<LoginRecord> records) {
        loginRecordRepository.deleteInBatch(records);
    }

    @Override
    public Long countLoginRecord() {
        return loginRecordRepository.count();
    }


    private void setModifyTime(LoginRecord loginRecord){
        Timestamp date = new Timestamp(System.currentTimeMillis());
        loginRecord.setModifyTime(date);
    }

    private void setCreateAndModifyTime(LoginRecord loginRecord){
        Timestamp date = new Timestamp(System.currentTimeMillis());
        loginRecord.setModifyTime(date);
        loginRecord.setCreateTime(date);
    }
}
