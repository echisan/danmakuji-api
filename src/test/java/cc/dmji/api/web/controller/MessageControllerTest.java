package cc.dmji.api.web.controller;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.entity.Message;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Status;
import cc.dmji.api.service.MessageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class MessageControllerTest extends ApiApplicationTests {

    @Autowired
    private MessageService messageService;

    @Test
    public void testSendSystem(){
        Message message = new Message();
        message.setType(MessageType.SYSTEM.name());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        message.setModifyTime(ts);
        message.setCreateTime(ts);
        message.setIsRead(MessageConstants.NOT_READ);
        message.setmStatus(Status.NORMAL.name());
        message.setContent("这是第一条系统通知");
        Message message1 = messageService.insertMessage(message);
        System.out.println(message1);
    }

}