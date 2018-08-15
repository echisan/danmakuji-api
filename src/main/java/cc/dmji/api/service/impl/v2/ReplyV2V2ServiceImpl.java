package cc.dmji.api.service.impl.v2;

import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.ReplyOrderBy;
import cc.dmji.api.enums.v2.ReplyType;
import cc.dmji.api.mapper.v2.ReplyMapper;
import cc.dmji.api.repository.v2.ReplyV2Repository;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.web.model.v2.reply.ReplyDTO;
import cc.dmji.api.web.model.v2.reply.ReplyDetail;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by echisan on 2018/7/26
 */
@Service
public class ReplyV2V2ServiceImpl implements ReplyV2Service {
    @Autowired
    private ReplyV2Repository replyV2Repository;

    @Resource
    private ReplyMapper replyMapper;

    @Override
    public ReplyV2 insert(ReplyDTO replyDTO) {

        ReplyV2 replyV2 = new ReplyV2();
        replyV2.setUserId(replyDTO.getUserId());
        replyV2.setFloor(replyDTO.getFloor());
        replyV2.setCreateTime(new Timestamp(System.currentTimeMillis()));
        replyV2.setContent(replyDTO.getContent());
        replyV2.setLikeCount(0L);
        replyV2.setObjectId(replyDTO.getObjectId());
        replyV2.setReplyType(replyDTO.getReplyType());
        replyV2.setRoot(replyDTO.getRoot());
        replyV2.setStatus(Status.NORMAL);
        replyV2.setTop(false);
        return replyV2Repository.save(replyV2);
    }

    @Override
    public ReplyV2 update(ReplyV2 replyV2) {
        return replyV2Repository.save(replyV2);
    }

    @Override
    public ReplyV2 getById(Long id) {
        return replyV2Repository.findById(id).orElse(null);
    }

    @Override
    public List<ReplyDetail> listByObjectIdAndType(Long oid, ReplyType rt, Long userId, ReplyOrderBy orderBy, Direction direction) {
        return this.listByObjectIdAndType(oid,rt,0L,userId,orderBy,direction);
    }

    @Override
    public Long countByObjectIdAndTypeAndRoot(Long oid, ReplyType rt, Long root) {
        return replyV2Repository.countByObjectIdEqualsAndReplyTypeEqualsAndRootEqualsAndStatusEquals(oid,rt.getCode(),root,Status.NORMAL);
    }

    @Override
    public Long countAllRepliesByObjectIdAndReplyType(Long oid, ReplyType rt, Status status) {
        return replyV2Repository.countByObjectIdEqualsAndReplyTypeEqualsAndStatusEquals(oid,rt.getCode(),status);
    }

    @Override
    public List<ReplyDetail> listByObjectIdAndType(Long oid, ReplyType rt, Long root, Long userId, ReplyOrderBy orderBy, Direction direction) {
        return replyMapper.listReplyDetailByOidAndType(oid,rt.getCode(),userId,root,Status.NORMAL.ordinal(),orderBy.name(),direction.name(),null);
    }

    @Override
    public Long countNowFloorByObjectAndType(Long oid, ReplyType rt) {
        return replyV2Repository.countByObjectIdEqualsAndReplyTypeEqualsAndRootEquals(oid, rt.getCode(), 0L);
    }

    @Override
    public Long countNowFloorByRootReplyId(Long oid, ReplyType rt) {
        return replyV2Repository.countByRootEqualsAndReplyTypeEquals(oid, rt.getCode());
    }

    @Override
    public ReplyDetail getTopReply(Long oid, ReplyType rt, Long userId) {
        Page<ReplyDetail> replyDetailPage = PageHelper.startPage(1,1,false).doSelectPage(()->{
           replyMapper.listReplyDetailByOidAndType(oid,rt.getCode(),userId,0L,Status.NORMAL.ordinal(),null,null,true);
        });
        if (replyDetailPage.size() != 0){
            return replyDetailPage.getResult().get(0);
        }
        return null;
    }

    @Override
    public Long countByRootAndFloorBetween(Long root, ReplyType rt, Long beginFloor, Long endFloor) {
        return replyV2Repository
                .countByRootEqualsAndReplyTypeEqualsAndFloorBetweenAndStatusEquals(
                        root, rt.getCode(),beginFloor,endFloor,Status.NORMAL,Sort.by(Sort.Direction.ASC,"floor")
                );
    }

    @Override
    public Long countByObjectIdAndFloorBetween(Long oid, ReplyType rt, Long beginFloor, Long endFloor) {
        return replyV2Repository.countByObjectIdEqualsAndReplyTypeEqualsAndFloorBetweenAndStatusEquals(
                oid,rt.getCode(),beginFloor,endFloor,Status.NORMAL,Sort.by(Sort.Direction.ASC,"floor")
        );
    }
}
