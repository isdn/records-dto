package dev.isdn.demo.records_dto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAutoConfiguration
@Configuration
@ComponentScan
@EnableTransactionManagement
public class App {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplicationBuilder(App.class).properties("spring.config.name:app").build();
        app.run(args);
    }
}
