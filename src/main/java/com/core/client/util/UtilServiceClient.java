package com.core.client.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "util-api",  url = "http://localhost:8084")
public interface UtilServiceClient {

    //일정 등록 접근을 위함
    @PostMapping("/calendars")
    ResponseEntity<Void> postCalendar(@RequestBody CalendarDto.Post dto,
                                      @RequestParam("departmentId") long departmentId);
}
