package com.core.attendance.repository;

import com.core.attendance.entity.Attendance;
import com.core.attendance.response.MonthlyWorkResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndAttendanceStatus(Long employeeId, Attendance.AttendanceStatus attendanceStatus);

    Page<Attendance> findByEmployeeId(Long employeeId, Pageable pageable);

    //월별 근무시간 통계
    @Query("SELECT new com.core.attendance.response.MonthlyWorkResponse(a.employeeId, SUM(a.dailyWorkingTime)) " +
            "FROM Attendance a " +
            "WHERE a.employeeId = :employeeId AND FUNCTION('YEAR', a.checkInTime) = :year AND FUNCTION('MONTH', a.checkInTime) = :month " +
            "GROUP BY a.employeeId")
    List<MonthlyWorkResponse> findTotalWorkingTimePerEmployeeForMonth(
            @Param("employeeId") Long employeeId,
            @Param("year") int year,
            @Param("month") int month);
}
