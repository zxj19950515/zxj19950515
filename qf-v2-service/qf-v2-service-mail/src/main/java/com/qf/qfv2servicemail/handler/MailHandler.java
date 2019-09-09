package com.qf.qfv2servicemail.handler;

import com.qf.qfv2servicemail.service.MailServiceImpl;
import com.qf.v2.common.constant.RabbitConstant;
import com.qf.v2.entity.TUser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailHandler {

    @Autowired
    private MailServiceImpl mailService;
    @RabbitListener(queues = RabbitConstant.MAIL_QUEUE)
    public void mailReceiver(TUser user){
        System.out.println(user);
        mailService.sendActiveMail("发送邮件服务测试","253049790@qq.com",user);
    }
}
