package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.repository.ReplyRepository;
import cc.dmji.api.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by echisan on 2018/5/14
 */
@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    @Override
    public Reply insertReply(Reply reply) {
        Reply r = new Reply();
        r.setEpId(reply.getEpId());
        r.setContent(reply.getContent());
        r.setIsParent(reply.getIsParent());
        r.setParentId(reply.getParentId());
        r.setrHate(0);
        r.setrLike(0);
        r.setrStatus(reply.getrStatus());
        r.setrPage(reply.getrPage());
        r.setUserId(reply.getUserId());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        r.setCreateTime(ts);
        r.setModifyTime(ts);
        return replyRepository.save(r);
    }

    @Override
    public void deleteReplyById(String id) {
        replyRepository.deleteById(id);
    }

    @Override
    public List<Reply> listReplyByEpId(Integer epId) {
        return replyRepository.findRepliesByEpIdEquals(epId);
    }

    @Override
    public Page<Reply> listReplyByEpId(Integer epId, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return replyRepository.findByEpIdEquals(epId,pageable);
    }

    @Override
    public Reply getReplyById(String id) {
        return replyRepository.findById(id).orElse(null);
    }
}
