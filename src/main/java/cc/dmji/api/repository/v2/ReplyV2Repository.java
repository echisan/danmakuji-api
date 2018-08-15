package cc.dmji.api.repository.v2;

import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyV2Repository extends JpaRepository<ReplyV2, Long> {

    Long countByObjectIdEqualsAndReplyTypeEqualsAndRootEquals(Long oid, Integer replyType, Long root);

    Long countByRootEqualsAndReplyTypeEquals(Long root,Integer replyType);

    Long countByObjectIdEqualsAndReplyTypeEqualsAndStatusEquals(Long oid, Integer replyType, Status status);

    Long countByRootEqualsAndReplyTypeEqualsAndFloorBetweenAndStatusEquals(
            Long root, Integer replyType, Long beginFloor, Long endFloor, Status status,Sort sort);

    Long countByObjectIdEqualsAndReplyTypeEqualsAndFloorBetweenAndStatusEquals(
            Long oid, Integer replyType, Long beginFloor,Long endFloor, Status status, Sort sort
    );

    Long countByObjectIdEqualsAndReplyTypeEqualsAndRootEqualsAndStatusEquals(Long oid,Integer replyType,Long root, Status status);

}
