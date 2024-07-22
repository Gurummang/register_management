package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "errorCode",
        "errorMessage",
        "id",
        "name",
        "alias",
        "status",
        "adminEmail",
        "apiToken",
        "validation",
        "webhookUrl",
        "registerDate"
})
public class OrgSaasResponse {

    private Integer errorCode;
    private String errorMessage;

    // Workspace_config
    private Integer id;
    private String alias;
    private String adminEmail;
    private String apiToken;
    private Boolean validation;
    private String webhookUrl;
    private Timestamp registerDate;

    // OrgSaas
    private Integer orgSaasId;
    private Integer orgId;
    private String saasId;
    private String name;
    private Integer status;
    private String securityScore;

    // POST(regi, modify, delete)
    public OrgSaasResponse(Integer errorCode, String errorMessage, Integer id, Timestamp registerDate) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.id = id;
        this.registerDate = registerDate;
    }
    // GET(mkUrl)
    public OrgSaasResponse(Integer errorCode, String errorMessage, String webhookUrl) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.webhookUrl = webhookUrl;
    }
    // GET(list)
    public OrgSaasResponse(Integer id, String name, String alias, Integer status,
                           String adminEmail, String apiToken, Boolean validation,
                           String webhookUrl, Timestamp registerDate) {

        this.id = id;
        this.name = name;
        this.alias = alias;
        this.status = status;
        this.adminEmail = adminEmail;
        this.apiToken = apiToken;
        this.validation = validation;
        this.webhookUrl = webhookUrl;
        this.registerDate = registerDate;
    }

}
