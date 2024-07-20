package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrgSaasRequest {

    // Workspace_config
    @JsonProperty("configId")
    private Integer config_id;       // workspace_config.id
    private String workspace_name;   // alias랑 다른게 뭐지?
    @JsonProperty("alias")
    private String alias;            // 최소단위가 workspace인 시점에서 이게 필요한가?
    @JsonProperty("adminEmail")
    private String saas_admin_email;
    @JsonProperty("webhookUrl")
    private String webhook_url;
    @JsonProperty("apiToken")
    private String token;

    // OrgSaas
    @JsonProperty("orgId")
    private Integer org_id;
    @JsonProperty("saasId")
    private Integer saas_id;
    private String space_id;
}
