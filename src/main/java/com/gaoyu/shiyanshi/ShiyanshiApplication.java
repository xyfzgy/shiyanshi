package com.gaoyu.shiyanshi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gaoyu.shiyanshi.mapper")
public class ShiyanshiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiyanshiApplication.class, args);
    }

}
