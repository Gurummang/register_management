package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgSaasResponse {

    private String message;
    // Workspace_config
    private Integer config_id;      // workspace_config.id
    private String workspace_name;
    private String alias;
    private String saas_admin_email;
    private String webhook;
    private Timestamp register_date;
    private String token;           // 이건 굳이?
//  private String validation;    // 나중에
    // OrgSaas
    private Integer org_saas_id;
    private Integer org_id;
    private Integer saas_id;
    private Integer status;         // 연동 상태
    private String security_score;
    private String saas_name;
// private String message; // "[Success / Failure]: ~~~ " -> 나중에 하자, 급한거 아님

    // POST(regi, modify)
    public OrgSaasResponse(String message,Integer config_id, String workspace_name, Timestamp register_date) {
        this.message = message;
        this.config_id = config_id;
        this.workspace_name = workspace_name;
        this.register_date = register_date;
    }

    // POST(delete)
    public OrgSaasResponse(String message, Integer config_id) {
        this.message = message;
        this.config_id = config_id;
    }

    public OrgSaasResponse(String message, Integer saas_id, Integer config_id, Integer status,
                           String workspace_name, String token, String webhook,
                           String saas_admin_email, Timestamp register_date) {

        this.message = message;
        this.saas_id = saas_id;
        this.config_id = config_id;
        this.status = status;

        this.workspace_name = workspace_name;
        this.token = token;
        this.webhook = webhook;
        this.saas_admin_email = saas_admin_email;
        this.register_date = register_date;
    }

}
