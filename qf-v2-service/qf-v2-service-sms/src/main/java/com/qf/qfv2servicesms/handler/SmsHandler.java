package com.qf.qfv2servicesms.handler;

import com.qf.qfv2servicesms.service.SmsServiceImpl;
import com.qf.v2.common.constant.RabbitConstant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsHandler {

    @Autowired
    private SmsServiceImpl smsService;
    @RabbitListener(queues = RabbitConstant.SMS_QUEUE)
    public void mailReceiver(String phone, String content){
//        System.out.println(user);
//        mailService.sendActiveMail("发送邮件服务测试","253049790@qq.com",user);
        smsService.sendSms(phone,content);
    }
}
