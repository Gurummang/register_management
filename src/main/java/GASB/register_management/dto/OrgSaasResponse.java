package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrgSaasResponse {

    private boolean ok;
    private Integer errorCode;
    private String errorMessage;

    // Workspace_config
    private Integer configId;
    private String alias;
    private String adminEmail
    private String apiToken;
    private String validation;
    private String webhookUrl;
    private Timestamp registerDate;

    // OrgSaas
    private Integer orgSaasId;
    private Integer orgId;
    private String saasId;
    private String saasName;
    private Integer status;
    private String securityScore;

    // POST(regi, modify, delete)
    public OrgSaasResponse(Boolean ok, Integer errorCode, String errorMessage, Integer configId, Timestamp registerDate) {
        this.ok = ok;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.configId = configId;
        this.registerDate = registerDate;
    }
    // GET(mkUrl)
    public OrgSaasResponse(Boolean ok, Integer errorCode, String errorMessage, String webhookUrl) {
        this.ok = ok;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.webhookUrl = webhookUrl;
    }
    // GET(list)
    public OrgSaasResponse(Boolean ok, Integer errorCode, String errorMessage,
                           Integer configId, String saasName, String alias, Integer status,
                           String adminEmail, String apiToken, String validation, String webhookUrl, Timestamp registerDate) {
        this.ok = ok;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.configId = configId;
        this.saasName = saasName;
        this.alias = alias;
        this.status = status;
        this.adminEmail = adminEmail;
        this.apiToken = apiToken;
        this.validation = validation;
        this.webhookUrl = webhookUrl;
        this.registerDate = registerDate;
    }

}
