package com.qf.qfv2websearch;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class QfV2WebSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV2WebSearchApplication.class, args);
    }

}
