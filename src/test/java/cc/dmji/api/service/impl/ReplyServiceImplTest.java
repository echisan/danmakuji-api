package cc.dmji.api.service.impl;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.entity.Reply;
import cc.dmji.api.service.ReplyService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
@Rollback
public class ReplyServiceImplTest extends ApiApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(ReplyServiceImplTest.class);

    @Autowired
    private ReplyService replyService;

    @Test
    public void insertReply() {
        Reply reply = new Reply();
        reply.setUserId("4028e381635e269c01635e26b1a80000");
        reply.setrPage(1);
        reply.setrStatus("NORMAL");
        reply.setEpId(3);
        reply.setParentId("");
        reply.setIsParent((byte) 1);
        Reply r = replyService.insertReply(reply);
        assertNotNull(r);
    }

    @Test
    public void deleteReplyById() {
        assertNull(replyService.getReplyById("4028e381635eaae401635eab203f0000"));
    }

    @Test
    public void listReplyByEpId() {
        List<Reply> replies = replyService.listReplyByEpId(3);
        assertNotNull(replies);
    }

    @Test
    public void getReplyById() {
        Reply reply = replyService.getReplyById("4028e381635eaae401635eab203f0000");
        assertNotNull(reply);
    }

    @Test
    public void listPageableReplyById(){
        Page<Reply> replyPage = replyService.listReplyByEpId(3,0,10);
        assertNotNull(replyPage);
    }
}
