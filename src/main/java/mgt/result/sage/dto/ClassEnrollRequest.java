package mgt.result.sage.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClassEnrollRequest {

    private String enrollmentClassName;

    private Long courseId;

    private List<Long> studentIds;

    private List<Long> magisterIds;

    private LocalDate startDate;
}
