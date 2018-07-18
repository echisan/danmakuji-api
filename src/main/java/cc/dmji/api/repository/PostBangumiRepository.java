package cc.dmji.api.repository;

import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostBangumiRepository extends JpaRepository<PostBangumi, Long> {

    Page<PostBangumi> findByUserIdEqualsAndStatusEquals(String userId, Status status, Pageable pageable);

    Page<PostBangumi> findByUserIdEqualsAndStatusEqualsAndPostBangumiStatusEquals(
            String userId, Status status, PostBangumiStatus postBangumiStatus, Pageable pageable);

    Page<PostBangumi> findByPostBangumiStatusEqualsAndStatusEquals(PostBangumiStatus postBangumiStatus, Status status, Pageable pageable);

    Page<PostBangumi> findByStatusEquals(Status status, Pageable pageable);

    List<PostBangumi> findByBangumiNameEquals(String bangumiName);


    Page<PostBangumi> findByUserIdEqualsAndStatusEqualsAndBangumiNameLike(String userId, Status status, String bangumiName,Pageable pageable);

    Page<PostBangumi> findByUserIdEqualsAndStatusEqualsAndPostBangumiStatusEqualsAndBangumiNameLike(
            String userId, Status status, PostBangumiStatus postBangumiStatus,String bangumiName, Pageable pageable);

}
