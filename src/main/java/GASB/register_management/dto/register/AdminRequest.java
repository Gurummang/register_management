package GASB.register_management.dto.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRequest {

    private Integer id;
    @JsonProperty("orgId")
    private Integer org_id;
    @JsonProperty("firstName")
    private String first_name;
    @JsonProperty("lastName")
    private String last_name;
    // For Regi, Modify, Delete
    private String email;
    private String password;
}
