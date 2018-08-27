package cc.dmji.api.service;

import cc.dmji.api.entity.v2.SysMessage;
import cc.dmji.api.enums.v2.SysMsgTargetType;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public interface SysMessageService {

    SysMessage insert(SysMessage sysMessage);

    SysMessage update(SysMessage sysMessage);

    Long countNewSysMessage(Long uid, Timestamp userCreateTime, SysMsgTargetType sysMsgTargetType);

    List<SysMessage> listNewSysMessages(Long uid, Timestamp userCreateTime, SysMsgTargetType sysMsgTargetType);

    SysMessage getById(Long id);

    Page<SysMessage> listSysMessages(Integer pn, Integer ps);
}
