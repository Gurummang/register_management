package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgRequest {

    private Integer id;
    @JsonProperty("orgName")
    private String org_name;
}
