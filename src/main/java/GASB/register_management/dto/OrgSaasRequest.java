package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgSaasRequest {

    // For Register
    private Integer org_id;     //
    private Integer saas_id;

    // For modify & delete
    private Integer org_saas_id;

    // For Register & modify
    private String saas_admin_email;
    private String webhook_url;
    private String api_key;
    // private String nickname;  // 필요 없나?
    private String saas_alias;
}
