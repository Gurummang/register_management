package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgSaasResponse {

    // private String message; // "[Success / Failure]: ~~~ " -> 나중에 하자, 급한거 아님

    private Integer org_saas_id;
    private Timestamp ts;
    private Integer regi_status;     // 등록 상태
    private String webhook_url;
    private String saas_admin_email;
    private String saas_alias;

    private String err_message;

    // POST(register)
    public OrgSaasResponse(Integer org_saas_id, String saas_admin_email, Timestamp ts) {

        this.org_saas_id = org_saas_id;
        this.saas_admin_email = saas_admin_email;
        this.ts = ts;
    }

//    // POST(Modify)
//    public OrgSaasResponse(String status, Integer org_saas_id, Integer regi_status, String webhook_url, String saas_admin_email, String saas_alias) {
//
//        this.status = status;
//        this.org_saas_id = org_saas_id;
//        this.regi_status = regi_status;
//        this.webhook_url = webhook_url;
//        this.saas_admin_email = saas_admin_email;
//        this.saas_alias = saas_alias;
//    }
//    // POST (Delete)
//    public OrgSaasResponse(String status, Integer org_saas_id, String saas_admin_email) {
//
//        this.status = status;
//        this.org_saas_id = org_saas_id;
//        this.saas_admin_email = saas_admin_email;
//    }
//    // POST (예외)
//    public OrgSaasResponse(String status, String message) {
//        this.status = status;
//        this.message = message;
//    }
//    // GET getOrgSaasList()
//    public OrgSaasResponse(String status, Integer org_saas_id, Integer regi_status, String webhook_url, String saas_admin_email, String saas_alias, Timestamp ts) {
//
//        this.status = status;
//        this.org_saas_id = org_saas_id;
//        this.regi_status = regi_status;
//        this.webhook_url = webhook_url;
//        this.saas_admin_email = saas_admin_email;
//        this.saas_alias = saas_alias;
//        this.ts = ts;
//    }
}
