package mgt.result.sage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseData {
    private Long id;
    private String code;
    private String title;
    private int creditUnit;
}
