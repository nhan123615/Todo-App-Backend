package com.todo.restfulapi.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * author Nhanle
 * */
@Component
public class EmailUtils {
    @Value("${spring.mail.host}")
    private String HOST;
    @Value("${spring.mail.port}")
    private String PORT;
    @Value("${spring.mail.username}")
    private String USERNAME;
    @Value("${spring.mail.password}")
    private String PASSWORD;


    public  boolean send(String to, String subject, String content) {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host",  HOST);
            properties.put("mail.smtp.port",PORT);
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }

            });

            // mail declare
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content, "text/html");
            message.setSentDate(new Date());
            Transport.send(message);

            return true;
        } catch (Exception e) {
            return false;

        }
    }

}
