package cc.dmji.api.service.impl;

import cc.dmji.api.ApiApplicationTests;
import cc.dmji.api.entity.Message;
import cc.dmji.api.service.MessageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@Rollback(value = true)
public class MessageServiceImplTest extends ApiApplicationTests {

    @Autowired
    private MessageService messageService;

    @Test
    public void insertMessage() {
        Message message = new Message();
        message.setEpId(3);
        message.setType("type");
        message.setmStatus("m_status");
        message.setIsRead((byte) 1);
        message.setUserId("4028e381635e269c01635e26b1a80000");
        Message m = messageService.insertMessage(message);
        assertNotNull(m);

    }

    @Test
    public void deleteMessageById() {
        messageService.deleteMessageById("4028e381635f0b8d01635f0ba4ce0000");
        Message message = messageService.getMessageById("4028e381635f0b8d01635f0ba4ce0000");
        assertNull(message);
    }

    @Test
    public void updateMessage() {
        Message message = messageService.getMessageById("4028e381635f0b8d01635f0ba4ce0000");
        message.setType("newType");
        Message newMessage = messageService.updateMessage(message);
        assertEquals(message.getType(),newMessage.getType());
    }

    @Test
    public void getMessageById() {
        Message message = messageService.getMessageById("4028e381635f0b8d01635f0ba4ce0000");
        assertNotNull(message);
    }

    @Test
    public void listMessageByUserId() {
    }
}
