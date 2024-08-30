package GASB.register_management.dto.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaasRequest {

    private Integer id;     // saas 테이블의 id
    private String saasName;
}
