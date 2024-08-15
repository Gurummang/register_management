package GASB.register_management.util;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.service.imple.OrgSaasServiceImple;
import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
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
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GoogleUtil {

    private static final String APPLICATION_NAME = "grummang-google-drive-func";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private final OrgSaasService orgSaasService;

    @Autowired
    public GoogleUtil(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, OrgSaasService orgSaasService) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.orgSaasService = orgSaasService;
    }

    public void func(String code) {
        try {
            // 1. 코드로 토큰 반환 및 크리덴셜 객체 생성
            Credential credential = getCredential(code);

            System.out.println("Accesss Token: " + credential.getAccessToken());
            System.out.println("Refresh Token: " + credential.getRefreshToken());

            try {
                // 2. credential로 공유 드라이브 객체 생성
                Drive drive = getDriveService(credential);

                List<String[]> drives = getAllSharedDriveIdsAndNames(drive);

                // 드라이브 목록이 비어있거나 조건에 맞지 않는 경우 DELETE 삽입
                if (drives.isEmpty()) {
                    drives.add(new String[]{"DELETE"});
                }

                orgSaasService.updateOrgSaasGD(drives, credential.getAccessToken());
            } catch (Exception e) {
                // 예외 발생 시, 드라이브 목록에 DELETE 상태 추가
                List<String[]> drives = new ArrayList<>();
                drives.add(new String[]{"DELETE"});  // DELETE 상태 추가
                orgSaasService.updateOrgSaasGD(drives, null);
            }
        } catch (Exception e) {
            // 예외 발생 시, 드라이브 목록에 DELETE 상태 추가
            List<String[]> drives = new ArrayList<>();
            drives.add(new String[]{"DELETE"});  // DELETE 상태 추가
            orgSaasService.updateOrgSaasGD(drives, null);
        }
    }


    public Drive getDriveService(Credential credential) throws Exception {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            log.error("An error occurred while creating the Drive service: {}", e.getMessage());
            throw new RuntimeException("Failed to create Drive service", e);
        }
    }

    public List<String[]> getAllSharedDriveIdsAndNames(Drive drive) throws IOException {
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

    public Credential getCredential(String code) throws Exception {
        try {
            // 코드로 token 요청 객체 빌드
            // .newTokenRequest 메서드로 구글 인증 서버에 요청
            // googleAuthor~ 리팩토링하면 좋을듯
            GoogleTokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                    .setRedirectUri("https://back.grummang.com/api/v1/org-saas/token")
                    .execute();

            System.out.println(tokenResponse);

            // 얻어온 토큰으로 Credential 빌드
            // 빌드는 구글 라이브러리의 메서드로
            // 인자는 토큰 반환 객체
            return googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, "user");
        } catch (TokenResponseException e) {
            log.error("Error obtaining token response: {}", e.getMessage());
            throw new RuntimeException("Failed to obtain token response", e);
        } catch (IOException e) {
            log.error("IO Exception during token exchange: {}", e.getMessage());
            throw new RuntimeException("Failed to obtain token", e);
        }
    }
}