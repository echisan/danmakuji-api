package cc.dmji.api.service;

import cc.dmji.api.entity.OnlineRecord;

import java.util.Date;
import java.util.List;

public interface OnlineRecordService {

    OnlineRecord insert(OnlineRecord onlineRecord);

    List<OnlineRecord> insertRecords(List<OnlineRecord> onlineRecords);

    void deleteOnlineRecordById(Long id);

    void deleteOnlineRecords(List<OnlineRecord> onlineRecords);

    OnlineRecord getById(Long id);

    List<OnlineRecord> getAll();

    List<OnlineRecord> getAll(Date begin, Date end);
}
