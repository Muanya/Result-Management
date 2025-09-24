package mgt.result.sage.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EnrollmentDetail {

    private  Long id;
    private String enrollmentClassName;
    private List<UserDetail> students;
    private List<UserDetail> magisters;
    private CourseData course;
    private LocalDate startDate;

}
