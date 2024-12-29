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
        SpringApplication.run(PersonalBudgetTrackerApplication.class, args);
    }

}
