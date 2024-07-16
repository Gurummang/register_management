package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgSaasResponse {

    // 공통
    private Integer org_saas_id;

    // register
    private String status;
    private String valid;       // Email & API key 검사
    private Timestamp ts;

    // modify
    // private String status;
    // private String valid;    // Email & API Key not matching~

    // delete
    // private String status;

    // get
    // org_saas_id
    // name
    private String regi_status;
    private String webhook_url;
    private String saas_admin_email;
    private String saas_alias;
    // register_date

    // POST(register)
    public OrgSaasResponse(String status, Integer org_saas_id, String valid, Timestamp ts) {

        //
        this.status = status;
        this.org_saas_id = org_saas_id;
        //
        this.valid = valid;
        this.ts = ts;
    }

    // GET /api/v1/org-saas/{saasId}/mkUrl
    // 버튼 누를 때 생성
    //
    public OrgSaasResponse(String status, String webhook_url) {
        this.status = status;
        this.webhook_url = webhook_url; // https://grum.com/saas/uuid
    }


    // GET getOrgSaasList()
    public OrgSaasResponse(String status, Integer org_saas_id, String valid, Integer regi_satus, String webhook_url, String saas_admin_email, Timestamp ts) {
        this.status = status;
        this.org_saas_id = org_saas_id;
        this.valid = valid;
        this.regi_status = regi_status;
        this.webhook_url = webhook_url;
        this.saas_admin_email = saas_admin_email;
        this.ts = ts;
    }


}
