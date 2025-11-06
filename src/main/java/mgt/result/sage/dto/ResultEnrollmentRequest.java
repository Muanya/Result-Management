package mgt.result.sage.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultEnrollmentRequest {
    private Long enrollmentId;
    private Long courseId;
    private List<Long> studentIds;
}
