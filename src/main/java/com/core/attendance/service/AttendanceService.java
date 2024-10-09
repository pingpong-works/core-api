package com.core.attendance.service;

import com.core.attendance.entity.Attendance;
import com.core.attendance.repository.AttendanceRepository;
import com.core.client.auth.AuthServiceClient;
import com.core.client.auth.EmployeeData;
import com.core.client.auth.UserResponse;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.utils.PageableCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AuthServiceClient authServiceClient;

    public AttendanceService(AttendanceRepository attendanceRepository, AuthServiceClient authServiceClient) {
        this.attendanceRepository = attendanceRepository;
        this.authServiceClient = authServiceClient;
    }

    public Attendance checkIn(Long employeeId, String ipAddress) {

        log.info(ipAddress);
        //ip 주소 확인
        if( !isVerifiedIp(ipAddress)) {
            throw new BusinessLogicException(ExceptionCode.NOT_ALLOWED_IP) ;
        }

        // 직원 확인
        verifiedEmployee(employeeId);

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setAttendanceStatus(Attendance.AttendanceStatus.CLOCKED_IN);

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long employeeId, String ipAddress) {

        log.info(ipAddress);
        //ip 주소 확인
        if( !isVerifiedIp(ipAddress)) {
            throw new BusinessLogicException(ExceptionCode.NOT_ALLOWED_IP) ;
        }

        // 직원 확인
        verifiedEmployee(employeeId);

        Optional<Attendance> attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceStatus(employeeId, Attendance.AttendanceStatus.CLOCKED_IN);

        if (attendance.isPresent()) {
            Attendance findAttendance = attendance.get();

            findAttendance.setEmployeeId(employeeId);
            findAttendance.setCheckOutTime(LocalDateTime.now());
            findAttendance.setAttendanceStatus(Attendance.AttendanceStatus.CLOCKED_OUT);

            return attendanceRepository.save(findAttendance);

        } else {
            throw new BusinessLogicException(ExceptionCode.CAN_NOT_FIND_ATTENDANCE);
        }
    }

    public Page<Attendance> findMyAttendance(Long employeeId, int page, int size,
                                             String criteria, String direction) {
        Pageable pageable = PageableCreator.createPageable(page, size, criteria, direction);

        return attendanceRepository.findByEmployeeId(employeeId, pageable);
    }

    public Page<Attendance> findAttendances(int page, int size,
                                           String criteria, String direction) {
        Pageable pageable = PageableCreator.createPageable(page, size, criteria, direction);

        return attendanceRepository.findAll(pageable);
    }

    private boolean isVerifiedIp(String ipAddress) {
        //사내 ip 리스트

        List<String> companyIps = List.of("127.0.0.1", "0:0:0:0:0:0:0:1");
        return companyIps.contains(ipAddress);
    }

    //검증된 회원인지 확인
    public EmployeeData verifiedEmployee(Long employeeId) {
        UserResponse employeeResponse = getEmployee(employeeId);

        EmployeeData employee = employeeResponse.getData();  // 내부의 data 필드 접근

        // null 체크 및 필수 필드 확인
        if (employee == null || employee.getName() == null || employee.getEmployeeId() == null || employee.getDepartmentName() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }
        return employee;
    }

    public UserResponse getEmployee(Long employeeId) {
        UserResponse response = authServiceClient.getEmployeeByIdForUser(employeeId);

        log.debug("Response from Feign: {}", response);

        if(response == null || response.getData() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        return response;
    }

}