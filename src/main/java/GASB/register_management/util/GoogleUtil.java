package GASB.register_management.util;

import GASB.register_management.service.register.OrgSaasService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.DriveList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GoogleUtil {

    private static final String APPLICATION_NAME = "grummang-google-drive-func";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private final OrgSaasService orgSaasService;

    @Autowired
    public GoogleUtil(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, OrgSaasService orgSaasService) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.orgSaasService = orgSaasService;
    }

    public void func(String code) {
        try {
            Credential credential = getCredential(code);
            try {
                Drive drive = getDriveService(credential);
                List<String[]> drives = getAllSharedDriveIdsAndNames(drive);

                if (drives.isEmpty()) {
                    drives.add(new String[]{"DELETE"});
                }

                orgSaasService.updateOrgSaasGD(drives, credential.getAccessToken());
            } catch (IOException e) {
                log.error("Failed to retrieve Google Drive data: {}", e.getMessage());
                updateWithDelete();
            } catch (NullPointerException e) {
                log.error("Unexpected null value: {}", e.getMessage());
                updateWithDelete();
            } catch (RuntimeException e) {
                log.error("Unexpected runtime error: {}", e.getMessage());
                updateWithDelete();
            }
        } catch (RuntimeException e) {
            log.error("Failed to obtain credentials: {}", e.getMessage());
            updateWithDelete();
        } catch (Exception e) {
            log.error("General error: {}", e.getMessage());
            updateWithDelete();
        }
    }

    private void updateWithDelete() {
        List<String[]> drives = new ArrayList<>();
        drives.add(new String[]{"DELETE"});
        orgSaasService.updateOrgSaasGD(drives, "");
    }

    private Drive getDriveService(Credential credential) {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException e) {
            log.error("Failed to create Drive service: {}", e.getMessage());
            throw new RuntimeException("Failed to create Drive service", e);
        } catch (GeneralSecurityException e) {
            log.error("Security configuration error: {}", e.getMessage());
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

    private Credential getCredential(String code) {
        try {
            GoogleTokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            return googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, "user");
        } catch (TokenResponseException e) {
            log.error("Token response error: {}", e.getMessage());
            throw new RuntimeException("Failed to obtain token response", e);
        } catch (IOException e) {
            log.error("Failed to obtain token: {}", e.getMessage());
            throw new RuntimeException("Failed to obtain token", e);
        }
    }
}