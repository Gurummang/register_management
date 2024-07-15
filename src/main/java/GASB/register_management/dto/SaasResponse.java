package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaasResponse {

    private String status;
    private String message;
    private Integer id;
    private String saas_name;

    // Constructor for POST responses
    public SaasResponse(String status, String message, Integer id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }

    // Constructor for GET responses
    public SaasResponse(Integer id, String saas_name) {
        this.id = id;
        this.saas_name = saas_name;
    }
}
