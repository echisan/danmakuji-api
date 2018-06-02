package cc.dmji.api.repository;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.web.model.ReplyInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String>
        ,PagingAndSortingRepository<Reply,String> {

    List<Reply> findRepliesByEpIdEquals(Integer epId);

    Page<Reply> findByEpIdEquals(Integer epId, Pageable pageable);

    Long countByEpIdEquals(Integer epId);

    Long countByEpIdAndIsParentEquals(Integer epId, Byte isParent);

    Long countByReplyId(String replyId);

}
