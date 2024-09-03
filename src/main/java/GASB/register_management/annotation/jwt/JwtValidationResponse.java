package GASB.register_management.annotation.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtValidationResponse implements Serializable {
    private final String email;
    private final String status;
}
