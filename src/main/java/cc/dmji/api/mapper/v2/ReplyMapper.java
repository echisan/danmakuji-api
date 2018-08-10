package cc.dmji.api.mapper.v2;

import cc.dmji.api.web.model.v2.reply.ReplyDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReplyMapper {

    List<ReplyDetail> listReplyDetailByOidAndType(@Param("oid") Long objectId,
                                                  @Param("type") Integer replyType,
                                                  @Param("uid") Long userId,
                                                  @Param("root") Long root,
                                                  @Param("status") Integer status,
                                                  @Param("orderBy") String orderBy,
                                                  @Param("direction") String direction,
                                                  @Param("top") Boolean top);
}
