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
//@JsonPropertyOrder({
//        "errorCode",
//        "errorMessage",
//        "id",
//        "name",
//        "alias",
//        "status",
//        "adminEmail",
//        "apiToken",
//        "validation",
//        "webhookUrl",
//        "registerDate"
//})
public class OrgSaasResponse {

    // about Error
    private Integer errorCode;
    private String errorMessage;

    // OrgSaas
    private String name;    // saasName
    private Integer status;
    private String securityScore;

    // Workspace_config
    private Integer id;     // configId
    private String alias;
    private String adminEmail;
    private String apiToken;
    private String webhookUrl;
    private Timestamp registerDate;

    // token(email) valid
    private Boolean validation;

    // GET(mkUrl) & POST(valid)
    public OrgSaasResponse(Integer errorCode, String errorMessage, Boolean validation, String webhookUrl) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.webhookUrl = webhookUrl;
    }
    // POST(regi, modify, delete)
    public OrgSaasResponse(Integer errorCode, String errorMessage, Integer id, Timestamp registerDate) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.id = id;
        this.registerDate = registerDate;
    }
    // GET(list)
    public OrgSaasResponse(Integer id, String name, String alias, Integer status,
                           String adminEmail, String apiToken,
                           String webhookUrl, Timestamp registerDate) {

        this.id = id;
        this.name = name;
        this.alias = alias;
        this.status = status;
        this.adminEmail = adminEmail;
        this.apiToken = apiToken;
        this.webhookUrl = webhookUrl;
        this.registerDate = registerDate;
    }

}
