package cc.dmji.api.mapper;

import cc.dmji.api.entity.v2.SysMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface SysMessageMapper {
    Long countNewSysMessage(@Param("uid")Long uid,
                            @Param("userCreateTime")Timestamp userCreateTime,
                            @Param("sysMsgTargetType") Integer sysMsgTargetType,
                            @Param("type")Integer messageType);

    List<SysMessage> listNewSysMessages(@Param("uid")Long uid,
                                        @Param("userCreateTime")Timestamp userCreateTime,
                                        @Param("sysMsgTargetType") Integer sysMsgTargetType,
                                        @Param("type")Integer messageType);
}
