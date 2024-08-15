package GASB.register_management.util;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.repository.WorkspaceRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.DriveList;
import com.google.api.services.drive.model.About;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public GoogleUtil(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, WorkspaceRepository workspaceRepository,
                      OrgSaasRepository orgSaasRepository, SaasRepository saasRepository) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.workspaceRepository = workspaceRepository;
        this.orgSaasRepository = orgSaasRepository;
        this.saasRepository = saasRepository;
    }

    public OrgSaasResponse starter(OrgSaasRequest orgSaasRequest) throws Exception {
        Credential credential;
        try {
            // 리스너 호출 && Credential 객체 반환
            credential = getCredentials();
        } catch (Exception e) {
            log.error("Error while getting credentials: {}", e.getMessage());
            throw new RuntimeException("Failed to obtain Google credentials", e);
        }

        String accessToken = credential.getAccessToken();
        try {
            Drive drive = getDriveService(credential);
            List<String[]> sharedDrives = getAllSharedDriveIdsAndNames(drive);
            for (String[] driveInfo : sharedDrives) {
                OrgSaas orgSaas = new OrgSaas();
                Workspace workspace = new Workspace();
                orgSaas.setOrgId(orgSaasRequest.getOrgId());
                orgSaas.setSaasId(orgSaasRequest.getSaasId());
                orgSaas.setSpaceId(driveInfo[0]);
                OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);
                workspace.setId(regiOrgSaas.getId());
                workspace.setSpaceName(driveInfo[1]);
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setApiToken(accessToken);
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace regiWorkspace = workspaceRepository.save(workspace);
            }
        } catch (Exception e) {
            log.error("Error while processing Drive data: {}", e.getMessage());
            throw new RuntimeException("Failed to process Drive data", e);
        }

        return null;
    }

    private Drive getDriveService(Credential credential) throws Exception {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            log.error("An error occurred while creating the Drive service: {}", e.getMessage());
            throw new RuntimeException("Failed to create Drive service", e);
        }
    }

    private List<String[]> getAllSharedDriveIdsAndNames(Drive drive) throws IOException {
        List<String[]> sharedDrivesInfo = new ArrayList<>();
        DriveList driveList = drive.drives().list().execute();

        if (driveList.getDrives() != null && !driveList.getDrives().isEmpty()) {
            for (com.google.api.services.drive.model.Drive sharedDrive : driveList.getDrives()) {
                String[] driveInfo = new String[]{sharedDrive.getId(), sharedDrive.getName()};
                sharedDrivesInfo.add(driveInfo);
            }
        }
        return sharedDrivesInfo;
    }

    protected Credential getCredentials() throws Exception {
        try {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(8088)
                    .setCallbackPath("/login/oauth2/code/google")
                    .build();
            return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, receiver).authorize("user");
        } catch (Exception e) {
            log.error("Error during Google OAuth2 authorization: {}", e.getMessage());
            throw new RuntimeException("Failed to obtain Google credentials", e);
        }
    }
}