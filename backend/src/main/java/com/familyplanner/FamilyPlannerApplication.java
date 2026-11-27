package com.familyplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FamilyPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyPlannerApplication.class, args);
    }
}
