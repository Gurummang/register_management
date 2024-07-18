package GASB.register_management.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrgSaasRequest {

    // Workspace_config
    private Integer config_id;       // workspace_config.id
    private String workspace_name;   // alias랑 다른게 뭐지?
    private String alias;            // 최소단위가 workspace인 시점에서 이게 필요한가?
    private String saas_admin_email;
    private String webhook_url;
    private String token;

    // OrgSaas
    private Integer org_saas_id;
    private Integer org_id;
    private Integer saas_id;
    private String space_id;



}
