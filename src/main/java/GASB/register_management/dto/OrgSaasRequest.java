package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgSaasRequest {

    // OrgSaas
    private Integer orgId;
    private Integer saasId;

    // Workspace_config
    private Integer id;
    private String alias;
    private String adminEmail;
    private String apiToken;
    private String webhookUrl;
}
