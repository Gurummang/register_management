package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgSaasResponse {

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
    private Integer status;         // 연동 상태
    private String security_score;

// private String message; // "[Success / Failure]: ~~~ " -> 나중에 하자, 급한거 아님

    // POST(register)
    public OrgSaasResponse(Integer config_id, String workspace_name) {
        this.config_id = config_id;
        this.workspace_name = workspace_name;
    }

}
