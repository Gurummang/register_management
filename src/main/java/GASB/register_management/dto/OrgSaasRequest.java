package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgSaasRequest {

    // POST // Register
    private Integer org_id;
    private Integer saas_id;

    // POST // Modify & Delete
    private Integer org_saas_id;
    private String saas_admin_email;
    private String webhook_url;
    private String api_key;
    private String saas_alias;

//    // POST register
//    public void regiOrgSaasRequest(Integer org_id, Integer saas_id, String saas_admin_email,
//                          String api_key, String webhook_url, String saas_alias) {
//        // required
//        this.org_id = org_id;
//        this.saas_id = saas_id;
//        this.saas_admin_email = saas_admin_email;
//        // optional
//        this.api_key = api_key;
//        this.webhook_url = webhook_url;
//        this.saas_alias = saas_alias;
//    }
//    // POST modify
//    public OrgSaasRequest(Integer org_saas_id, String saas_admin_email, String api_key,
//                          String webhook_url, String saas_alias) {
//        // required
//        this.org_saas_id = org_saas_id;
//        this.saas_admin_email = saas_admin_email;
//        // optional
//        this.api_key = api_key;
//        this.webhook_url = webhook_url;
//        this.saas_alias = saas_alias;
//    }
//    // POST delete
//    public OrgSaasRequest(Integer org_saas_id, String saas_admin_email) {
//        this.org_saas_id = org_saas_id;
//        this.saas_admin_email = saas_admin_email;
//    }
//    // GET list
//    public OrgSaasRequest(Integer org_id) {
//        this.org_id = org_id;
//    }
}
