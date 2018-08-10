package cc.dmji.api.mapper;

import cc.dmji.api.web.model.admin.PostBangumiInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by echisan on 2018/7/10
 */
@Mapper
public interface PostBangumiMapper {

    List<PostBangumiInfo> listPostBangumi(@Param("status") String status,
                                          @Param("postBangumiStatus") String postBangumiStatus,
                                          @Param("beginTime")Timestamp beginTime,
                                          @Param("endTime") Timestamp endTime,
                                          @Param("orderBy")String orderBy,
                                          @Param("direction")String direction);
}
