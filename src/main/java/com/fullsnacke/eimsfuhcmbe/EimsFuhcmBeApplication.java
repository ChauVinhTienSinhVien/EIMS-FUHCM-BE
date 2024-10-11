package com.fullsnacke.eimsfuhcmbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;

@SpringBootApplication(scanBasePackages = {"com.fullsnacke.eimsfuhcmbe.converter", "com.fullsnacke.eimsfuhcmbe"})
public class EimsFuhcmBeApplication {

    public static void main(String[] args) {
        ZoneId defaultZone = ZoneId.systemDefault();
        System.out.println("System default timezone: " + defaultZone);
        SpringApplication.run(EimsFuhcmBeApplication.class, args);
    }

}
