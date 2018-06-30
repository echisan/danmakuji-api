package cc.dmji.api;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.repository.ReplyRepository;
import cc.dmji.api.service.ReplyService;
import cc.dmji.api.web.model.ReplyInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

}
