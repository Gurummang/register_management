package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "message", "config_id", "saasName", "alias", "status",
        "saas_admin_email", "token", "validation", "webhook", "register_date"
})
public class OrgSaasResponse {

    private String message;
    // Workspace_config
    @JsonProperty("id")
    private Integer config_id;      // workspace_config.id
    private String workspace_name;
    private String alias;
    @JsonProperty("adminEmail")
    private String saas_admin_email;
    @JsonProperty("webhookUrl")
    private String webhook;
    @JsonProperty("registerDate")
    private Timestamp register_date;
    @JsonProperty("apiToken")
    private String token;           // 이건 굳이?
    private String validation;    // 나중에
    // OrgSaas
    private Integer org_saas_id;
    private Integer org_id;
    @JsonProperty("saasId")
    private Integer saas_id;
    private String saasName;
    private Integer status;         // 연동 상태
    private String security_score;
    private String saas_name;
// private String message; // "[Success / Failure]: ~~~ " -> 나중에 하자, 급한거 아님

    // POST(regi, modify)
    public OrgSaasResponse(String message,Integer config_id, String alias, Timestamp register_date) {
        this.message = message;
        this.config_id = config_id;
        this.alias = alias;
        this.register_date = register_date;
    }

    // POST(delete)
    public OrgSaasResponse(String message, Integer config_id) {
        this.message = message;
        this.config_id = config_id;
    }

    public OrgSaasResponse(String message, Integer config_id, String saas_name, String alias, Integer status,
                           String saas_admin_email, String token, String validation, String webhook,
                            Timestamp register_date) {

        this.message = message;
        this.config_id = config_id;
        this.saasName = saas_name;
        this.alias = alias;
        this.status = status;
        this.saas_admin_email = saas_admin_email;
        this.token = token;
        this.validation = validation;
        this.webhook = webhook;
        this.register_date = register_date;
    }

    public OrgSaasResponse(String message, String webhook) {
        this.message = message;
        this.webhook = webhook;
    }
}
