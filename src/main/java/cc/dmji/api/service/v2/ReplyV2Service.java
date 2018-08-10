package cc.dmji.api.service.v2;

import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.v2.ReplyOrderBy;
import cc.dmji.api.enums.v2.ReplyType;
import cc.dmji.api.web.model.v2.reply.ReplyDTO;
import cc.dmji.api.web.model.v2.reply.ReplyDetail;

import java.util.List;

public interface ReplyV2Service {

    ReplyV2 insert(ReplyDTO replyDTO);

    ReplyV2 update(ReplyV2 replyV2);

    ReplyV2 getById(Long id);

    List<ReplyDetail> listByObjectIdAndType(Long oid, ReplyType rt, Long root, Long userId, ReplyOrderBy orderBy, Direction direction);

    List<ReplyDetail> listByObjectIdAndType(Long oid, ReplyType rt, Long root, ReplyOrderBy orderBy, Direction direction);

    Long countByObjectIdAndType(Long oid, ReplyType rt, Long root);

    Long countNowFloorByObjectAndType(Long oid, ReplyType rt);

    Long countNowFloorByRootReplyId(Long root, ReplyType rt);

    ReplyDetail getTopReply(Long oid,ReplyType rt,Long userId);

    Long countByRootAndFloorBetween(Long root, ReplyType rt, Long beginFloor, Long endFloor);

    Long countByObjectIdAndFloorBetween(Long oid, ReplyType rt, Long beginFloor, Long endFloor);

}
