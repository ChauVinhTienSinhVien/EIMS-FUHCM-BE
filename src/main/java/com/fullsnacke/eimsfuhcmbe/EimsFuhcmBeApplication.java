package com.fullsnacke.eimsfuhcmbe;

import com.fullsnacke.eimsfuhcmbe.configuration.ConfigurationHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;

@SpringBootApplication(scanBasePackages = {"com.fullsnacke.eimsfuhcmbe.converter", "com.fullsnacke.eimsfuhcmbe"})
public class EimsFuhcmBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EimsFuhcmBeApplication.class, args);
    }

}
