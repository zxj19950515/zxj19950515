package com.qf.qfv2serviceproduct;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.qf.v2.mapper")
public class QfV2ServiceProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV2ServiceProductApplication.class, args);
    }

}
