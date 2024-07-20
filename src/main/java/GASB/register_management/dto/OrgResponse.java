package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgResponse {

    private String status;
    private String message;
    private Integer id;
    @JsonProperty("orgName")
    private String org_name;

    // POST
    public OrgResponse(String status, String message, Integer id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }
    // GET
    public OrgResponse(Integer id, String org_name) {
        this.id = id;
        this.org_name = org_name;
    }
}
