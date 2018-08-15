package cc.dmji.api.mapper.v2;

import cc.dmji.api.enums.Status;
import cc.dmji.api.web.model.v2.message.MessageDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<MessageDetail> listMessages(@Param("uid")Long uid,
                                     @Param("status")Integer status,
                                     @Param("type")Integer messageType);


}
