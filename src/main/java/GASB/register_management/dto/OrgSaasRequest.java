package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgSaasRequest {

    private Integer config_id;

    // POST // Register
    private Integer org_id;
    private Integer saas_id;

    // POST // Modify & Delete
    private Integer org_saas_id;
    private String saas_admin_email;
    private String webhook_url;
    private String api_key;
    private String saas_alias;

}
