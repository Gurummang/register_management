package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaasResponse {

    private String status;
    private String messeage;
    private Integer id;
    private String saas_name;

    // Response for POST
    public SaasResponse(String status, String messeage, Integer id) {
        this.status = status;
        this.messeage = messeage;
        this.id = id;
    }

    // Response for GET
    public SaasResponse(Integer id, String saas_name){
        this.id = id;
        this.saas_name = saas_name;
    }
}
