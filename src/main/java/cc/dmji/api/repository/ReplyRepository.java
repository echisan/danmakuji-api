package cc.dmji.api.repository;

import cc.dmji.api.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long>
        , PagingAndSortingRepository<Reply, Long> {

    List<Reply> findRepliesByEpIdEquals(Long epId);

    Page<Reply> findByEpIdEquals(Long epId, Pageable pageable);

    Long countByEpIdEquals(Long epId);

    Long countByEpIdEqualsAndIsParentEqualsAndRStatusEquals(Long epId, Byte isParent, String status);

    Long countByReplyId(Long replyId);

    Long countByParentIdEqualsAndRStatusEquals(Long parentId, String status);

    Long countByCreateTimeBetween(Date begin, Date end);

    Long countByEpIdEqualsAndParentIdEqualsAndRStatusEquals(Long epId, Long parentId, String status);

    Long countByEpIdEqualsAndFloorBetweenAndIsParentEqualsAndRStatusEquals(Long epId, Long begin, Long end, Byte isParent, String status);

    Long countByParentIdEqualsAndCreateTimeBetween(Long parentId, Date begin, Date end);


    Long countByEpIdEqualsAndRStatusEqualsAndIsParentEqualsAndCreateTimeBetween(Long epId, String status, Byte isParent, Date begin, Date end);

    @Query(value = "select * from dm_reply where parent_id = ?1 and r_status=?2 order by create_time desc limit 0,1", nativeQuery = true)
    Reply getLatestSonReplyByParentId(Long parentId, String status);

    @Query(value = "select * from dm_reply where parent_id = ?1 and r_status=?2 order by create_time asc  limit 0,1", nativeQuery = true)
    Reply getFirstSonReplyByParentId(Long parent, String status);

    @Query(value = "select * from dm_reply where ep_id = ?1 and r_status=?2 and is_parent=1 order by create_time desc limit 0,1", nativeQuery = true)
    Reply getLatestParentReplyByEpId(Long epId, String status);

    @Query(value = "select * from dm_reply where ep_id = ?1 and r_status=?2 and is_parent=1 order by create_time asc limit 0,1", nativeQuery = true)
    Reply getFirstParentReplyByEpId(Long epId, String status);

    List<Long> countByReplyIdIn(Collection<Long> replyIds);

}
