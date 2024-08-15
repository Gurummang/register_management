package GASB.register_management.controller;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.util.GoogleUtil;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.drive.Drive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173, http://localhost:8080"})
@RestController
@Slf4j
@RequestMapping("/api/v1/org-saas")
public class OrgSaasController {

    private final OrgSaasService orgSaasService;
    private final GoogleUtil googleUtil;
    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @Autowired
    public OrgSaasController(OrgSaasService orgSaasService, GoogleUtil googleUtil, GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow) {
        this.orgSaasService = orgSaasService;
        this.googleUtil = googleUtil;
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
    }

    @PostMapping("/slackValid")
    public OrgSaasResponse slackValid (@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.slackValid(orgSaasRequest);
    }

    @GetMapping("/{saasId}/mkUrl")
    public OrgSaasResponse mkUrl(@PathVariable Integer saasId) {
        return orgSaasService.getUrl(saasId);
    }

    @PostMapping("/register")
    public OrgSaasResponse register(@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.registerOrgSaas(orgSaasRequest);
    }

    @PostMapping("/modify")
    public OrgSaasResponse modify(@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.modifyOrgSaas(orgSaasRequest);
    }

    @PostMapping("/delete")
    public OrgSaasResponse delete(@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.deleteOrgSaas(orgSaasRequest);
    }

    @GetMapping("/{orgId}")
    public List<OrgSaasResponse> getOrgSaasList(@PathVariable Integer orgId) {
        return orgSaasService.getOrgSaasList(orgId);
    }

    @GetMapping("/token")
    public void token(@RequestParam("code") String code){
        googleUtil.func(code);
    }
}
