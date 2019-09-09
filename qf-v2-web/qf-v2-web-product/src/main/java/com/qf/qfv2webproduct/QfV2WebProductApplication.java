package com.qf.qfv2webproduct;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDubbo
@Import(FdfsClientConfig.class)
public class QfV2WebProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV2WebProductApplication.class, args);
    }

}
