package com.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.core", "com.alarm"})
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class CoreApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(CoreApiApplication.class, args);
  }
}
