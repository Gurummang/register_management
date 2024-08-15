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

        try {
            // 리스너 호출 && Credential 객체 반환
            Credential credential = getCredentials();
            String accessToken = credential.getAccessToken();
            System.out.println("AccessToken: " + accessToken);

            try {
                Drive drive = getDriveService(credential);
                System.out.println("Drive: " + drive);

                // 팀 드라이브 정보 얻기
                List<String[]> sharedDrives = getAllSharedDriveIdsAndNames(drive);
                for (String[] driveInfo : sharedDrives) {
                    OrgSaas orgSaas = new OrgSaas();
                    Workspace workspace = new Workspace();

                    System.out.println("Drive ID: " + driveInfo[0] + ", Drive Name: " + driveInfo[1]);

                    orgSaas.setOrgId(orgSaasRequest.getOrgId());
                    orgSaas.setSaasId(orgSaasRequest.getSaasId());
                    orgSaas.setSpaceId(driveInfo[0]);
                    OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);
                    System.out.println("저장된 Id: " + regiOrgSaas.getId());

                    workspace.setId(regiOrgSaas.getId());
                    workspace.setSpaceName(driveInfo[1]);
                    workspace.setAlias(orgSaasRequest.getAlias());
                    workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                    workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                    workspace.setApiToken(accessToken);  // 액세스 토큰을 워크스페이스에 저장
                    workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                    Workspace regiWorkspace = workspaceRepository.save(workspace);
                    System.out.println("저장된 ID: " + regiWorkspace.getId());
                }

                // My Drive 정보 얻기
//                String[] myDriveInfo = getMyDriveIdAndName(drive);
//                System.out.println("My Drive ID (Storage Quota Limit): " + myDriveInfo[0]);
//                System.out.println("My Drive Name (User Name): " + myDriveInfo[1]);

            } catch (Exception e) {
                log.error(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            log.error("Error while getting credential: {}", e.getMessage());
            throw e;
        }

        return null;
    }

    // 구글 Drive 서비스 객체 생성
    private Drive getDriveService(Credential credential) throws Exception {
        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            log.error("An error occurred while creating the Drive service: {}", e.getMessage());
            throw e;
        }
    }

    private List<String[]> getAllSharedDriveIdsAndNames(Drive drive) throws IOException {
        List<String[]> sharedDrivesInfo = new ArrayList<>();
        DriveList driveList = drive.drives().list().execute();  // 모든 팀 드라이브 가져오기

        // 팀 드라이브가 존재하는 경우
        if (driveList.getDrives() != null && !driveList.getDrives().isEmpty()) {
            for (com.google.api.services.drive.model.Drive sharedDrive : driveList.getDrives()) {
                // 각 팀 드라이브의 ID와 이름을 배열로 저장
                String[] driveInfo = new String[]{sharedDrive.getId(), sharedDrive.getName()};
                sharedDrivesInfo.add(driveInfo);  // 리스트에 추가
            }
        }
        // 팀 드라이브 목록을 반환
        return sharedDrivesInfo;
    }

//    // My Drive (기본 드라이브)의 ID와 이름을 가져오는 메서드
//    private String[] getMyDriveIdAndName(Drive drive) throws IOException {
//        About about = drive.about().get().setFields("user, storageQuota").execute();  // My Drive 정보 가져오기
//        String driveId = about.getStorageQuota().getLimit().toString();  // My Drive ID는 일반적으로 표시되지 않으므로 용량 제한을 ID처럼 사용
//        String driveName = about.getUser().getDisplayName();  // My Drive의 소유자 이름
//        return new String[]{driveId, driveName};
//    }

    // 인증 코드 리스너 && 인증 요청 메서드
    protected Credential getCredentials() throws Exception {
        // 인증 코드 리스너 생성
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8088)
                .setCallbackPath("/login/oauth2/code/google")
                .build();

        System.out.println("Receiver:" + receiver);
        System.out.println("Flow: " + googleAuthorizationCodeFlow);

        // 인증 요청 및 Credential 반환
        return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, receiver).authorize("user");
    }
}
