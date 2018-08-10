package cc.dmji.api.service;

import cc.dmji.api.entity.UserLogRecord;
import cc.dmji.api.enums.Role;

import java.util.List;

public interface UserLogRecordService {

    UserLogRecord insertUserLogRecord(UserLogRecord userLogRecord);

    void deleteUserLogRecordById(Long id);

    void deleteUserLogRecordByIds(List<UserLogRecord> userLogRecords);

    UserLogRecord updateUserLogRecord(UserLogRecord userLogRecord);

    UserLogRecord getUserLogRecordById(Long id);

    List<UserLogRecord> listUserLogRecords(Role role, Integer pn, Integer ps);

    List<UserLogRecord> listUserLogRecords();

    List<UserLogRecord> insertUserLogRecords(List<UserLogRecord> userLogRecords);
}
