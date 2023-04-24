package com.mytypeworldcup.mytypeworldcup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MytypeworldcupApplication {

    public static void main(String[] args) {
        SpringApplication.run(MytypeworldcupApplication.class, args);
    }

}
