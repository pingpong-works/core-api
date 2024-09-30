package com.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CoreApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(CoreApiApplication.class, args);
  }
}
