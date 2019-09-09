package com.qf.qfv2servicesearch.config;

import com.qf.v2.common.constant.RabbitConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    //声明队列
    @Bean
    public Queue getQueue(){

        return new Queue(RabbitConstant.PRODUCT_ADD_QUEUE,true,false,false);
    }

    //声明交换机
    @Bean
    public TopicExchange getTopicExchange(){

        return new TopicExchange(RabbitConstant.PRODUCT_EXCHANGE);
    }

    //绑定队列和交换机关系
    @Bean
    public Binding getBinding(Queue getQueue,TopicExchange getTopicExchange){
        return BindingBuilder.bind(getQueue).to(getTopicExchange).with("product.add");
    }

}
