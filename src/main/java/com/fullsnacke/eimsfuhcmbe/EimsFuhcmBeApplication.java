package com.fullsnacke.eimsfuhcmbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.fullsnacke.eimsfuhcmbe.converter", "com.fullsnacke.eimsfuhcmbe"})
public class EimsFuhcmBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EimsFuhcmBeApplication.class, args);
    }

}
