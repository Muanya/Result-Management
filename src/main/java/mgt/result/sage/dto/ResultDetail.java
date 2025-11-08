package mgt.result.sage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultDetail {
    private Long id;
    private double score;
    private String grade;
    private Long studentId;
    private Long enrollmentId;
    private Long courseId;
    private String studentName;
}
