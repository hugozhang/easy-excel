package me.about.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class Jemail {

    private Jemail() {

    }

    private static class JemailHolder {
       private static JavaMailSenderImpl sender = new JavaMailSenderImpl();
    }

    public Jemail builder() {
       return new Jemail();
    }

    private String host;

    private int port = 25;

    private String username;

    private String password;

    private String[] to;

    private String subject;

    private String text;

    public Jemail host(String host) {
        this.host = host;
        return this;
    }

    public Jemail port(int port) {
        this.port = port;
        return this;
    }

    public Jemail username(String username) {
        this.username = username;
        return this;
    }

    public Jemail password(String password) {
        this.password = password;
        return this;
    }

    public Jemail to(String to) {
        this.to = new String[]{to};
        return this;
    }

    public Jemail subject(String subject) {
        this.subject = subject;
        return this;
    }

    public Jemail text(String text) {
        this.text = text;
        return this;
    }

    public void send() {

        Assert.notNull(host,"`host` must not be null");
        Assert.notNull(username,"`username` must not be null");
        Assert.notNull(password,"`password` must not be null");
        Assert.notNull(subject,"`subject` must not be null");
        Assert.notNull(text,"`text` must not be null");
        Assert.notNull(to,"`to` must not be empty");

        JavaMailSenderImpl sender = JemailHolder.sender;
        sender.setDefaultEncoding("utf-8");
        sender.setPort(port);
        sender.setHost(host);
        sender.setUsername(username);
        sender.setPassword(password);

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth","true");
        properties.setProperty("mail.smtp.timeout","6000");
        properties.setProperty("mail.smtp.port",port + "");
        properties.setProperty("mail.smtp.socketFactory.port", port + "");
        properties.setProperty("mail.smtp.socketFactory.fallback","false");
        properties.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");

        try {
            MimeMessage mime = sender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mime, true, "utf-8");
            messageHelper.setFrom(username);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text,true);
            sender.send(mime);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException("email send failure.");
        }
    }

    public static void main(String[] args) {
        new Jemail().builder().host("smtp.163.com")
                .username("zxh117170@163.com")
                .password("123qwe")
                .subject("测试")
                .text("hello juma")
                .to("381129269@qq.com").send();

    }
}
