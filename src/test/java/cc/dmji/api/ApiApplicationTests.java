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

    @Test
    public void Test(){
        List<Reply> repliesByEpIdEquals = replyRepository.findRepliesByEpIdEquals(15);
        System.out.println(repliesByEpIdEquals.size());

        List<String> replyIds = new ArrayList<>();
        repliesByEpIdEquals.forEach(reply -> replyIds.add(reply.getReplyId()));

        List<Long> longs = replyRepository.countByReplyIdIn(replyIds);
        System.out.println(longs);
    }

    @Test
    public void test1(){
        List<String> ids = new ArrayList<>();
        ids.add("4028ef816446fc6c01644701228e000f");
        ids.add("4028ef8164470a1f0164473a3cda0004");
        ids.add("4028ef816446fc6c01644701e3250022");
//        Map<String, Long> stringLongMap = replyService.countByReplyIds(ids);
//        System.out.println(stringLongMap);

        List<ReplyInfo> replyInfos = replyService.listSonReplyInfoByParentIds(ids);
        System.out.println(replyInfos);
    }

    @Test
    public void mapperTest(){
        Page<PostBangumiInfo> postBangumiInfoPage = PageHelper.startPage(1,20).doSelectPage(() ->
                postBangumiService.listPostBangumi(Status.NORMAL, null, null, null, PostBangumiOrderBy.modifyTime, Direction.DESC));
//        List<PostBangumiInfo> postBangumiInfoList = postBangumiService.listPostBangumi(Status.NORMAL, null, null, null, null, Direction.DESC);

        System.out.println(postBangumiInfoPage);
    }

}
