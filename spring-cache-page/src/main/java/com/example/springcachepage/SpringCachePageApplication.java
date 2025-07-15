package com.example.springcachepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Kích hoạt tính năng caching trong Spring Boot
public class SpringCachePageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCachePageApplication.class, args);
    }

}
