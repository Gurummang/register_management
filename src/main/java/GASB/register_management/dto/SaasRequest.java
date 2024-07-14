package GASB.register_management.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaasRequest {

    private String action;  // POST 구분용
    private Integer id;
    private String saas_name;
}
