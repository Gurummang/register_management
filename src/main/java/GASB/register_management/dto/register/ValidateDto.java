package GASB.register_management.dto.register;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateDto {
    private String errorMessage;
    private String exceptionMessage;
    private Long orgId;
}