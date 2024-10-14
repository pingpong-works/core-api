package com.core.attendance.repository;

import com.core.attendance.entity.Attendance;
import com.core.attendance.response.MonthlyWorkResponse;
import com.core.attendance.response.WeeklyWorkResponse;
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

    // 월별 근무시간 통계
    @Query("SELECT new com.core.attendance.response.MonthlyWorkResponse(MONTH(a.checkInTime), SUM(a.dailyWorkingTime)) " +
            "FROM Attendance a WHERE a.employeeId = :employeeId AND YEAR(a.checkInTime) = :year " +
            "GROUP BY MONTH(a.checkInTime)")
    List<MonthlyWorkResponse> findTotalWorkingTimePerEmployeePerMonth(@Param("employeeId") Long employeeId,
                                                                      @Param("year") int year);

    // 주별 근무시간 통계
    @Query("SELECT new com.core.attendance.response.WeeklyWorkResponse(FUNCTION('WEEK', a.checkInTime), SUM(a.dailyWorkingTime)) " +
            "FROM Attendance a WHERE a.employeeId = :employeeId AND YEAR(a.checkInTime) = :year " +
            "GROUP BY FUNCTION('WEEK', a.checkInTime)")
    List<WeeklyWorkResponse> findTotalWorkingTimePerEmployeePerWeek(@Param("employeeId") Long employeeId,
                                                                    @Param("year") int year);
}
