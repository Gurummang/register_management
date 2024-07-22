package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgSaasRequest {

    // Workspace_config
    private Integer configId;
    private String alias;
    private String adminEmail;
    private String apiToken;
    private String webhookUrl;

    // OrgSaas
    private Integer orgId;
    private Integer saasId;
    private String saasName;
}
