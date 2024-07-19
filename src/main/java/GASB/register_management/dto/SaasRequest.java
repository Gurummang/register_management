package GASB.register_management.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaasRequest {

//    private String action;  // POST 구분용
    private Integer id;     // saas 테이블의 id
    @JsonProperty("saasName")
    private String saas_name;
}
