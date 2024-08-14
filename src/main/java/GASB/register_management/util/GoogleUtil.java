package GASB.register_management.util;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.entity.Org;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.repository.WorkspaceRepository;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GoogleUtil {

    private static final String APPLICATION_NAME = "grummang-google-drive-func";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private final WorkspaceRepository workspaceRepository;
    private final OrgSaasRepository orgSaasRepository;
    private final SaasRepository saasRepository;

    @Autowired
    public GoogleUtil(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, WorkspaceRepository workspaceRepository, OrgSaasRepository orgSaasRepository, SaasRepository saasRepository) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.workspaceRepository = workspaceRepository;
        this.orgSaasRepository = orgSaasRepository;
        this.saasRepository = saasRepository;
    }

    public OrgSaasResponse starter(OrgSaasRequest orgSaasRequest) {
        System.out.println("2. Call: starter");

        OrgSaas orgSaas = new OrgSaas();
        Workspace workspace = new Workspace();

        try {
            // 1. 토큰을 반환받음
            String accessToken = getDriveService();
            System.out.println(accessToken);

            // 2. 토큰을 받았으면 유저 정보를 저장
            try {
                orgSaas.setOrgId(orgSaasRequest.getOrgId());
                orgSaas.setSaasId(orgSaasRequest.getSaasId());
                orgSaas.setSpaceId("Test-" + UUID.randomUUID());
                OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);
                System.out.println("저장된 Id: " + regiOrgSaas.getId());

                workspace.setId(regiOrgSaas.getId());
                workspace.setSpaceName("Test-" + UUID.randomUUID());
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setApiToken(accessToken);  // 액세스 토큰을 워크스페이스에 저장
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace regiWorkspace = workspaceRepository.save(workspace);
                System.out.println("저장된 ID: " + regiWorkspace.getId());

//            String saasName = saasRepository.findById(orgSaasRequest.getSaasId()).get().getSaasName();

                return new OrgSaasResponse(200, null, regiWorkspace.getId(), regiWorkspace.getRegisterDate());

            } catch (Exception e) {
                return new OrgSaasResponse(199, e.getMessage(), null, null);
            }
        } catch (Exception e) {
            return new OrgSaasResponse(199, "Can Not receive Token", null, null);
        }
    }

    // 수정된 getDriveService 메서드
    private String getDriveService() throws Exception {
        try {
            System.out.println("3. Call: getDriveService");

            // getCredentials()을 호출하여 토큰을 반환받음
            String accessToken  = getCredentials().getAccessToken();

            System.out.println("Resp(Token): " + accessToken);
            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                    .setAccessToken(accessToken);

            System.out.println("Resp(Credential): " + credential);

            // 토큰을 반환
            return accessToken;

        } catch (Exception e) {
            log.error("An error occurred while connecting to the Drive service: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 인증 코드 리스너 && 인증 요청 메서드
    protected Credential getCredentials() throws Exception {
        System.out.println("Call: getCredentials");

        // 인증 코드 리스너 생성
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8088)
                .setCallbackPath("/login/oauth2/code/google")
                .build();

        System.out.println("Receiver:" + receiver);
        System.out.println("Flow: " + googleAuthorizationCodeFlow);

        // 인증 요청 및 Credential 반환
        System.out.print("여기인가?");
        return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, receiver).authorize("user");
    }
}
