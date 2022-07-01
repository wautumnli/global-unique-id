package com.ql.uniqueId;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ql.uniqueId.dao")
public class UniqueController {

    public static void main(String[] args) {
        SpringApplication.run(UniqueController.class, args);
    }
}