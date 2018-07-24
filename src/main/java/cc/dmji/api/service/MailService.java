package cc.dmji.api.service;

import javax.mail.MessagingException;

/**
 * Created by echisan on 2018/5/20
 */
public interface MailService {

    void sendVerifyEmail(String toEmail, Long userId, String uuid) throws MessagingException;
}
