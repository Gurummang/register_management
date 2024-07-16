package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgResponse {

    private String status;
    private String message;
    private Integer id;
    private String org_name;

    public OrgResponse(String status, String message, Integer id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }

    public OrgResponse(Integer id, String org_name) {
        this.id = id;
        this.org_name = org_name;
    }
}
