package com.qf.qfv2servicemail.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.v2.entity.TUser;
import com.qf.v2.mail.api.IMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Service
public class MailServiceImpl implements IMailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;


    @Override
    public void sendActiveMail(String subject, String to, TUser user) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=null;
        try {
            mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom("18117502917@163.com");
            mimeMessageHelper.setTo(to);

            Context context=new Context();
            context.setVariable("username",user.getUserName());
            //模板
            String info = templateEngine.process("active", context);
            mimeMessageHelper.setText(info,true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
            //异常处理相当主要
            //出现异常是正常的
            //收件人的地址有问题
            //邮件服务器有问题
            //网络出现问题
            //邮件服务器异常返回错误码 501 404

            //重试3次
            //将失败的信息保存到db
            //用定时任务轮训发送失败的表信息
            //ExceptionHandler.execute(T obj,String tableName)

        }

    }


}
