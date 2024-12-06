package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static final String NAMESPACE = "my-application";

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}