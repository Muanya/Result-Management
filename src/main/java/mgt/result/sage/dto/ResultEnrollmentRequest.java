package mgt.result.sage.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultEnrollmentRequest {
    private Long enrollmentId;
    private List<Long> studentIds;
}
