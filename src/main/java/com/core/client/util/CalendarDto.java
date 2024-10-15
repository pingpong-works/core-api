package com.core.client.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

public class CalendarDto {
    @Getter
    @AllArgsConstructor
    public static class Post {

        private String title;

        private String content;

        private LocalDateTime startTime;

        private LocalDateTime endTime;
    }
}
