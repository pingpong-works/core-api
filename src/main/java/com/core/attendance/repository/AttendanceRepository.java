package com.core.attendance.repository;

import com.core.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndAttendanceStatus(Long employeeId, Attendance.AttendanceStatus attendanceStatus);

    Page<Attendance> findByEmployeeId(Long employeeId, Pageable pageable);
}
