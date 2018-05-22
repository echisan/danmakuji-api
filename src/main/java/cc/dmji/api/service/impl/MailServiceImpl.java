package cc.dmji.api.service.impl;

import cc.dmji.api.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by echisan on 2018/5/20
 */
@Service
public class MailServiceImpl implements MailService {

    private static final String MAIL_CONTENT = "<html><body><a href='http://localhost:8080/auth/verify/uid/%s/key/%s' target='_blank'>单击此处完成注册</a><br><p>有效期30分钟</p></body></html>";

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendVerifyEmail(String toEmail, String userId, String uuid) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("Darker <" + from + ">");
        helper.setTo(toEmail);
        helper.setSubject("Welcome to become a Darker!");
        //language=HTML
        String href = String.format(MAIL_CONTENT, userId, uuid);
        helper.setText(href, true);
        javaMailSender.send(mimeMessage);
    }
}
