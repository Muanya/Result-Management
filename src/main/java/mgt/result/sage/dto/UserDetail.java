package mgt.result.sage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetail {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;
}
