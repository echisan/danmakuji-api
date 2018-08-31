package cc.dmji.api.mapper;

import cc.dmji.api.web.model.EpisodeDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EpisodeBangumiMapper {

    List<EpisodeDetail> listEpisodeDetailByEpIdIn(@Param("epIds") List<Long> epIds);

    EpisodeDetail getEpisodeDetailByEpId(@Param("epId") Long epId);
}
