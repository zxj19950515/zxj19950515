package com.qf.qfv2servicemail.config;

import com.qf.v2.common.constant.RabbitConstant;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    //声明队列
    @Bean
    public Queue getQueue(){
        return new Queue(RabbitConstant.MAIL_QUEUE,true,false,false);
    }

    @Bean
    public TopicExchange getTopicExchange(){
        return new TopicExchange(RabbitConstant.MAIL_EXCHANGE);
    }

    //绑定交换机和队列的关系
    @Bean
    public Binding getBinding(Queue getQueue,TopicExchange getTopicExchange){
        return BindingBuilder.bind(getQueue).to(getTopicExchange).with("mail.send");
    }
}
