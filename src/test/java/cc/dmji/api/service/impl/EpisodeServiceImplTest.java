package cc.dmji.api.service.impl;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.utils.EpisodePageInfo;
import cc.dmji.api.web.model.VideoInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@Rollback
public class EpisodeServiceImplTest extends ApiApplicationTests {
    @Autowired
    EpisodeService episodeService;

    @Test
    public void testService(){
//        EpisodePageInfo epinfo = episodeService.listEpisodes();
//        EpisodePageInfo ep1 = episodeService.listEpisodesByBangumiId(177);
//        EpisodePageInfo ep2 = episodeService.listEpisodesByBangumiId(177,1);
//        EpisodePageInfo ep3 = episodeService.listEpisodesByBangumiId(177,1,30);
//        System.out.println("end");
    }
}
