package com.core.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyWorkResponse {
    private int week;
    private Double totalWorkingTime;
}
