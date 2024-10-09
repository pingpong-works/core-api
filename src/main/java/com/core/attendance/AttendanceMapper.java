package com.core.attendance;

import com.core.attendance.entity.Attendance;
import com.core.attendance.response.AttendanceResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceResponse attendanceToResponse(Attendance attendance);
    List<AttendanceResponse> attendancesToResponses (List<Attendance> attendances);
}
