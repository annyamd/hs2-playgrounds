package com.example.hs2playgrounds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Hs2PlaygroundsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hs2PlaygroundsApplication.class, args);
    }

}
