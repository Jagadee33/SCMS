package com.college;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.college")
public class CollegeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollegeManagementApplication.class, args);
    }

}
