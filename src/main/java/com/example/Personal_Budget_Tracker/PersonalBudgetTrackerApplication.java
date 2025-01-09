package com.example.Personal_Budget_Tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example.Personal_Budget_Tracker",
        "com.example.Personal_Budget_Tracker.rest",
        "com.example.Personal_Budget_Tracker.core"
})
public class PersonalBudgetTrackerApplication {

    public static void main(String[] args) {
        // Add these debug lines before SpringApplication.run
        System.out.println("Database URL: " + System.getenv("SPRING_DATASOURCE_URL"));
        System.out.println("Database Username: " + System.getenv("SPRING_DATASOURCE_USERNAME"));
        System.out.println("Database Password is present: " + (System.getenv("SPRING_DATASOURCE_PASSWORD") != null));

        SpringApplication.run(PersonalBudgetTrackerApplication.class, args);
    }
}