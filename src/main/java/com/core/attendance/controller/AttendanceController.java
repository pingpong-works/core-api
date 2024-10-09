package com.core.attendance.controller;

import com.core.attendance.entity.Attendance;
import com.core.attendance.service.AttendanceService;
import com.core.client.auth.EmployeeData;
import com.core.client.auth.UserResponse;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
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

        return ResponseEntity.ok(attendance);
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

}
