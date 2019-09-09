package com.qf.qfv2webuser;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class QfV2WebUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV2WebUserApplication.class, args);
    }

}
