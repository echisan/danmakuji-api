package cc.dmji.api.service;

import cc.dmji.api.entity.LoginRecord;

import java.util.List;

public interface LoginRecordService {

    List<LoginRecord> listLoginRecordsByUserId(String userId);

    List<LoginRecord> listLoginRecordsByIp(String ip);

    LoginRecord getLoginRecordByRecordId(String recordId);

    LoginRecord insertLoginRecord(LoginRecord loginRecord);

    LoginRecord updateLoginRecord(LoginRecord loginRecord);

    void deleteLoginRecordByRecordId(String recordId);

    Long countLoginRecord();


}
