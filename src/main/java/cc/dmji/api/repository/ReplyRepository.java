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

public interface ReplyRepository extends JpaRepository<Reply, String>
        , PagingAndSortingRepository<Reply, String> {

    List<Reply> findRepliesByEpIdEquals(Integer epId);

    Page<Reply> findByEpIdEquals(Integer epId, Pageable pageable);

    Long countByEpIdEquals(Integer epId);

    Long countByEpIdEqualsAndIsParentEqualsAndRStatusEquals(Integer epId, Byte isParent, String status);

    Long countByReplyId(String replyId);

    Long countByParentIdEqualsAndRStatusEquals(String parentId, String status);

    Long countByCreateTimeBetween(Date begin, Date end);

    Long countByEpIdEqualsAndParentIdEqualsAndRStatusEquals(Integer epId, String parentId, String status);

    Long countByEpIdEqualsAndFloorBetweenAndIsParentEqualsAndRStatusEquals(Integer epId, Long begin, Long end, Byte isParent, String status);

    Long countByParentIdEqualsAndCreateTimeBetween(String parentId, Date begin, Date end);


    Long countByEpIdEqualsAndRStatusEqualsAndIsParentEqualsAndCreateTimeBetween(Integer epId, String status, Byte isParent, Date begin, Date end);

    @Query(value = "select * from dm_reply where parent_id = ?1 and r_status=?2 order by create_time desc limit 0,1", nativeQuery = true)
    Reply getLatestSonReplyByParentId(String parentId, String status);

    @Query(value = "select * from dm_reply where parent_id = ?1 and r_status=?2 order by create_time asc  limit 0,1", nativeQuery = true)
    Reply getFirstSonReplyByParentId(String parent, String status);

    @Query(value = "select * from dm_reply where ep_id = ?1 and r_status=?2 and is_parent=1 order by create_time desc limit 0,1", nativeQuery = true)
    Reply getLatestParentReplyByEpId(Integer epId, String status);

    @Query(value = "select * from dm_reply where ep_id = ?1 and r_status=?2 and is_parent=1 order by create_time asc limit 0,1", nativeQuery = true)
    Reply getFirstParentReplyByEpId(Integer epId, String status);

    List<Long> countByReplyIdIn(Collection<String> replyIds);

}
