package com.rao.study.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages="com.rao.study.sharding.mapper")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
