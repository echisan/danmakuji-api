package cc.dmji.api.service.impl;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.config.GlobalConfig;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.utils.BangumiPageInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback
public class BangumiServiceImplTest {

    @Autowired
    private BangumiService bangumiService;

    @Test
    public void testBangumi(){
//        BangumiPageInfo bpf = bangumiService.listBangumis();
//        BangumiPageInfo bpf_pn = bangumiService.listBangumis(2);
//        BangumiPageInfo bpf_pn_ps = bangumiService.listBangumis(1,30);
//        BangumiPageInfo byname = bangumiService.listBangumisByName("%长%");
//        BangumiPageInfo byname_pn = bangumiService.listBangumisByName("%长%",1);
//        BangumiPageInfo byname_pn_ps = bangumiService.listBangumisByName("%长%",1,30);
//        System.out.println("ok!");
    }

}
