package cc.dmji.api.service.impl;

import cc.dmji.api.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by echisan on 2018/5/20
 */
@Service
public class MailServiceImpl implements MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String MAIL_CONTENT = "<html><body><a href='http://localhost:8083/#/vemail?userId=%s&uuid=%s' target='_blank'>单击此处完成注册</a><br><p>有效期20分钟</p></body></html>";


    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendVerifyEmail(String toEmail, Long userId, String uuid) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("Darker <" + from + ">");
        helper.setTo(toEmail);
        helper.setSubject("Welcome to become a Darker!");
        //language=HTML
        String href = String.format(MAIL_CONTENT, userId, uuid);
        helper.setText(href, true);
        javaMailSender.send(mimeMessage);

        logger.debug("发送的邮件地址:{}", href);
    }

    @Override
    @Async
    public void sendVerifyCodeEmail(String toEmail, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("Darker <" + from + ">");
        helper.setTo(toEmail);
        helper.setSubject("请查收验证码!");
        helper.setText(content, true);
        javaMailSender.send(mimeMessage);
        logger.debug("发送的邮件地址:{}，验证码:{}", toEmail, content);
    }

    @Override
    public void sendEmail(String toEmail, String title, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("Darker <" + from + ">");
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content, true);
        javaMailSender.send(mimeMessage);
        logger.debug("发送的邮件地址:{},标题:{},正文:{}", toEmail, title, content);
    }
}
