package com.expense.manager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.expense.manager")
public class ManagerApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "9090");       
        SpringApplication.run(ManagerApplication.class, args);
    }
}