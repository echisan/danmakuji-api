package cc.dmji.api.service;

import cc.dmji.api.entity.LoginRecord;

import java.util.List;

public interface LoginRecordService {

    List<LoginRecord> listLoginRecordsByUserId(Long userId);

    List<LoginRecord> listLoginRecordsByIp(String ip);

    List<LoginRecord> listLoginRecordsByUserIdAndIp(Long userId, String ip);

    List<LoginRecord> listLoginRecords();

    LoginRecord getLoginRecordByRecordId(Long recordId);

    LoginRecord insertLoginRecord(LoginRecord loginRecord);

    LoginRecord updateLoginRecord(LoginRecord loginRecord);

    void deleteLoginRecordByRecordId(Long recordId);

    void deleteLoginRecords(List<LoginRecord> records);

    Long countLoginRecord();
}
