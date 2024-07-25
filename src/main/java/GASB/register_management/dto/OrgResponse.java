package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgResponse {

    private Integer errorCode;
    private String errorMessage;
    private Integer id;
    private String orgName;

    public OrgResponse(Integer errorCode, String errorMessage, Integer id, String orgName) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.id = id;
        this.orgName = orgName;
    }
}
