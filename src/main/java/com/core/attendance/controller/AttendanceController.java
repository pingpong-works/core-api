package com.core.attendance.controller;

import com.core.attendance.AttendanceMapper;
import com.core.attendance.entity.Attendance;
import com.core.attendance.response.MonthlyWorkResponse;
import com.core.attendance.service.AttendanceService;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.response.MultiResponseDto;
import com.core.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceMapper mapper;
    private final AttendanceService attendanceService;
    private static final String ATTENDANCE_DEFAULT_URL = "/attendances";

    public AttendanceController(AttendanceMapper mapper, AttendanceService attendanceService) {
        this.mapper = mapper;
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public ResponseEntity checkIn(@RequestParam Long employeeId, HttpServletRequest request) {

        Attendance attendance = attendanceService.checkIn(employeeId, ipConvertString(request));

        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/check-out")
    public ResponseEntity<Attendance> checkOut(@RequestParam Long employeeId, HttpServletRequest request) {
        Attendance attendance = attendanceService.checkOut(employeeId, ipConvertString(request));

        URI location = UriCreator.createUri(ATTENDANCE_DEFAULT_URL, attendance.getId());
        return ResponseEntity.created(location).build();
    }


    // 본인 근태 확인
    @GetMapping("/my-attendance")
    public ResponseEntity getAttendance (@RequestParam Long employeeId,
                                         @RequestParam @Positive int page, @RequestParam @Positive int size,
                                         @RequestParam(required = false) String direction,
                                         @RequestParam(required = false) String sort) {

        String criteria = "id";

        if(sort != null) {
            List<String> sorts = Arrays.asList("id", "attendanceStatus", "checkInTime", "checkOutTime");

            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Attendance> attendancePage = attendanceService.findMyAttendance(employeeId, page-1, size, criteria, direction);
        List<Attendance> attendanceList = attendancePage.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.attendancesToResponses(attendanceList),attendancePage), HttpStatus.OK);
    }

    // 관리자- 전직원 근태확인
    @GetMapping
    public ResponseEntity getAttendance (@RequestParam @Positive int page, @RequestParam @Positive int size,
                                         @RequestParam(required = false) String direction,
                                         @RequestParam(required = false) String sort) {
        String criteria = "id";

        if(sort != null) {
            List<String> sorts = Arrays.asList("id", "attendanceStatus", "checkInTime", "checkOutTime", "employeeId");

            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Attendance> attendancePage = attendanceService.findAttendances(page-1, size, criteria, direction);
        List<Attendance> attendanceList = attendancePage.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.attendancesToResponses(attendanceList),attendancePage), HttpStatus.OK);
    }

    private String ipConvertString (HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 헤더에 여러 개의 IP가 있을 경우 첫 번째 IP가 실제 클라이언트 IP

            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
        } else {
            ip = request.getHeader("Proxy-Client-IP");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }

        // IPv6 로컬호스트 주소를 IPv4로 변환
        if (isInternalIp(ip)) {
           return String.format("내부 ip : %s", ip);
        }
        return ip;
    }

    private boolean isInternalIp(String ipAddress) {
        // IPv6 로컬호스트
        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "127.0.0.1".equals(ipAddress)) {
            return false; //일단 허용
        }

        // IPv4 내부 IP
        if (ipAddress.startsWith("10.") ||
                ipAddress.startsWith("192.168.") ||
                (ipAddress.startsWith("172.") && Integer.parseInt(ipAddress.split("\\.")[1]) >= 16
                        && Integer.parseInt(ipAddress.split("\\.")[1]) <= 31)) {
            return true;
        }

        return false;
    }

    @GetMapping("/monthly")
    public ResponseEntity getMonthlyAttendance(@RequestParam int year,
                                               @RequestParam int month,
                                               @RequestParam Long employeeId) {
        List<MonthlyWorkResponse> statistics = attendanceService.getMonthlyAttendanceStatistics(employeeId, year, month);
        return ResponseEntity.ok(statistics);
    }

}
