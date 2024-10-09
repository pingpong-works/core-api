package com.core.attendance.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column
    private LocalDateTime checkInTime;

    @Column
    private LocalDateTime checkOutTime;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AttendanceStatus attendanceStatus = AttendanceStatus.CLOCKED_OUT;


    public enum AttendanceStatus {
        CLOCKED_IN("출근"),
        CLOCKED_OUT("퇴근");

        @Getter
        private String status;

        AttendanceStatus(String status) {
            this.status = status;
        }
    }
}
