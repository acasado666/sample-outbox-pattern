package com.kodebytes.acasado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OutboxDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutboxDemoApplication.class, args);
    }
}
