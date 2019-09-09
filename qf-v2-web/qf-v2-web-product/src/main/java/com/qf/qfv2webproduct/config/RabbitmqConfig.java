package com.qf.qfv2webproduct.config;

import com.qf.v2.common.constant.RabbitConstant;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Bean
    public TopicExchange getProductTopicExchange(){

        return new TopicExchange(RabbitConstant.PRODUCT_EXCHANGE);
    }

}
