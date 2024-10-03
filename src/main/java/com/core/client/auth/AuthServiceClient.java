package com.core.client.auth;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-api")
public interface AuthServiceClient {

    @GetMapping("/employees/{id}")
    EmployeeDto getEmployeeById(@PathVariable("id") Long employeeId);
}
