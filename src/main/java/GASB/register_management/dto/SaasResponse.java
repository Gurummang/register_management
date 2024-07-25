package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaasResponse {

    private Integer errorCode;
    private String errorMessage;
    private Integer id;
    private String saasName;

    public SaasResponse(Integer errorCode, String errorMessage, Integer id, String saasName) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.id = id;
        this.saasName = saasName;
    }
}
