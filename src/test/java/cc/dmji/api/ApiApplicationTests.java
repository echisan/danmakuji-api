package cc.dmji.api;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.v2.SysMessage;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.ReplyOrderBy;
import cc.dmji.api.enums.v2.ReplyType;
import cc.dmji.api.enums.v2.SysMsgTargetType;
import cc.dmji.api.repository.SysMessageRepository;
import cc.dmji.api.service.v2.MessageV2Service;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.web.model.v2.message.MessageDetail;
import cc.dmji.api.web.model.v2.reply.ReplyDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApiApplicationTests {

    @Autowired
    private ReplyV2Service replyV2Service;
    @Autowired
    private MessageV2Service messageV2Service;

    @Autowired
    private SysMessageRepository sysMessageRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() {
        List<ReplyDetail> replyDetails = replyV2Service.listByObjectIdAndType(66L, ReplyType.BANGUMI_EPISODE, 0L, ReplyOrderBy.floor, Direction.ASC);
        System.out.println(replyDetails);
    }

    @Test
    public void msgTest() {
        List<MessageDetail> messageDetails = messageV2Service.listMessages(2L, MessageType.LIKE);
        System.out.println(messageDetails);
    }

    @Test
    public void sysMsgTest(){
        SysMessage sysMessage = new SysMessage();
        sysMessage.setContent("这是一条只有manger才能看到的消息");
        sysMessage.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sysMessage.setPublisherUid(1L);
        sysMessage.setStatus(Status.NORMAL);
        sysMessage.setTitle("测试标题");
        sysMessage.setSysMsgTargetType(SysMsgTargetType.ADMIN.getCode());
        sysMessageRepository.save(sysMessage);
    }

    @Test
    public void redisTest(){
        String fwefsafa = stringRedisTemplate.opsForValue().get("fwefsafa");
        System.out.println(fwefsafa);
        System.out.println(fwefsafa == null);
    }
}
