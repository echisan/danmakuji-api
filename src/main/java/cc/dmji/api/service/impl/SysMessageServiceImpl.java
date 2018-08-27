package cc.dmji.api.service.impl;

import cc.dmji.api.entity.v2.SysMessage;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.v2.SysMsgTargetType;
import cc.dmji.api.mapper.SysMessageMapper;
import cc.dmji.api.repository.SysMessageRepository;
import cc.dmji.api.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
public class SysMessageServiceImpl implements SysMessageService {
    @Autowired
    private SysMessageRepository sysMessageRepository;
    @Resource
    private SysMessageMapper sysMessageMapper;

    @Override
    @Transactional
    public SysMessage insert(SysMessage sysMessage) {
        return sysMessageRepository.save(sysMessage);
    }

    @Override
    @Transactional
    public SysMessage update(SysMessage sysMessage) {
        return sysMessageRepository.save(sysMessage);
    }

    @Override
    public Long countNewSysMessage(Long uid, Timestamp userCreateTime, SysMsgTargetType sysMsgTargetType) {
        return sysMessageMapper
                .countNewSysMessage(uid, userCreateTime,
                        sysMsgTargetType.getCode(), MessageType.SYSTEM.getCode());
    }

    @Override
    public List<SysMessage> listNewSysMessages(Long uid, Timestamp userCreateTime, SysMsgTargetType sysMsgTargetType) {
        return sysMessageMapper
                .listNewSysMessages(uid, userCreateTime,
                        sysMsgTargetType.getCode(), MessageType.SYSTEM.getCode());
    }

    @Override
    public SysMessage getById(Long id) {
        return sysMessageRepository.findById(id).orElse(null);
    }

    @Override
    public Page<SysMessage> listSysMessages(Integer pn, Integer ps) {
        return sysMessageRepository.findAll(PageRequest.of(pn-1,ps, Sort.Direction.DESC,"createTime"));
    }
}
