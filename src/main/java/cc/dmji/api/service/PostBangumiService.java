package cc.dmji.api.service;

import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.PostBangumiOrderBy;
import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
import cc.dmji.api.web.model.admin.PostBangumiInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.util.List;

public interface PostBangumiService {

    PostBangumi insertPostBangumi(PostBangumi postBangumi);

    List<PostBangumi> insertPostBangumiList(List<PostBangumi> postBangumiList);

    void deletePostBangumiById(Long id);

    void deleteByPostBangumiList(List<PostBangumi> postBangumiList);

    Page<PostBangumi> listByUserId(String userId, Integer pn, Integer ps, PostBangumiStatus postBangumiStatus, Status status, Sort sort);

    Page<PostBangumi> listByUserId(String userId, Integer pn, Integer ps, Status status, Sort sort);

    // 默认根据创建时间的倒叙进行排序
    Page<PostBangumi> listByUserId(String userId, Integer pn, Integer ps, Status status);

    Page<PostBangumi> listPostBangumis(Integer pn,Integer ps,PostBangumiStatus postBangumiStatus,Status status,Sort sort);

    Page<PostBangumi> listPostBangumis(Integer pn,Integer ps,Status status,Sort sort);

    Page<PostBangumi> listPostBangumis(Integer pn,Integer ps,Status status);

    PostBangumi getById(Long id);

    List<PostBangumiInfo> listPostBangumi(Status status, PostBangumiStatus postBangumiStatus, Timestamp beginTime,
                                          Timestamp endTime, PostBangumiOrderBy orderBy, Direction direction);




}
