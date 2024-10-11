package com.core.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyWorkResponse {
    private Long employeeId;
    private Double totalWorkingTime;
}
