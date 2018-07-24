package cc.dmji.api;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.PostBangumiOrderBy;
import cc.dmji.api.enums.Status;
import cc.dmji.api.mapper.PostBangumiMapper;
import cc.dmji.api.repository.ReplyRepository;
import cc.dmji.api.service.PostBangumiService;
import cc.dmji.api.service.ReplyService;
import cc.dmji.api.utils.BangumiPageInfo;
import cc.dmji.api.web.model.Replies;
import cc.dmji.api.web.model.ReplyInfo;
import cc.dmji.api.web.model.admin.PostBangumiInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApiApplicationTests {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private PostBangumiService postBangumiService;

}
