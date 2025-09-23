package mgt.result.sage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthToken {
    private String accessToken;
    private String refreshToken;
}
