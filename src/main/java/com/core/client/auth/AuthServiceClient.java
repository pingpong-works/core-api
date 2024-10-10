package com.core.client.auth;


import com.core.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-api", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface AuthServiceClient {

    @GetMapping("/user/employees/{id}")
    UserResponse getEmployeeByIdForUser(@PathVariable("id") Long id);

}
