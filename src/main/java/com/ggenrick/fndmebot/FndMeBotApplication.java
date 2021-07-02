package com.ggenrick.fndmebot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class FndMeBotApplication {

    public static void main(String[] args) {

        SpringApplication.run(FndMeBotApplication.class, args);

    }


}

