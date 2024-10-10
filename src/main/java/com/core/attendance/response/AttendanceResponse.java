package com.core.attendance.response;


import com.core.attendance.entity.Attendance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class AttendanceResponse {
    private Long id;

    private Long employeeId;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private double dailyWorkingTime;

    private Attendance.AttendanceStatus attendanceStatus;
}
