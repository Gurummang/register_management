package GASB.register_management.util;

import GASB.register_management.entity.Workspace;
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

@Service
@Slf4j
public class GoogleUtil {

    private static final String APPLICATION_NAME = "grummang-google-drive-func";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public GoogleUtil(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, WorkspaceRepository workspaceRepository) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.workspaceRepository = workspaceRepository;
    }

    // 1.
    public Drive getDriveService(int workspaceId) throws Exception {
        try {
            System.out.println("Call: getDriveService");

            String accessToken  = getCredentials(workspaceId).getAccessToken();
            // build()로 토큰 db에 저장
            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                    .setAccessToken(accessToken);
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials(workspaceId))
            .setApplicationName(APPLICATION_NAME)
            .build();
        } catch (Exception e) {
            log.error("An error occurred while connecting to the Drive service: {}", e.getMessage(), e);
            throw e;
        }
    }
    // 1. 인증 코드 리스너 && 인증 요청 메서드
    protected Credential getCredentials(int workspaceId) throws Exception {
        System.out.println("Call: getCredentials");

        // NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        // 인증 코드 리스너
        // Login URL's Redirection URL == "~/login/oauth2/code/google:8888"
        // 즉 URL과 port를 사전에 협의하고 이를 열어놓고 대기하는 역할
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8088).setCallbackPath("/login/oauth2/code/google").build();
        System.out.println(receiver);
        System.out.println(googleAuthorizationCodeFlow);

        // 인증 요청
        return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, receiver).authorize("user");
    }
    //

//    // workspace.id로 해당 객체의 token을 가져옴
//    private String selectToken(int workspaceId){
//        try {
//            Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
//            // id에 해당하는 객체가 없을 경우
//            if(workspace == null) {
//                throw new IllegalStateException("Invalid workspace id: " + workspaceId);
//            }
//            // 토큰 반환
//            return workspace.getApiToken();
//        } catch (Exception e) {
//            log.error("An error occurred while selecting the token: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//    private TokenResponse refreshToken(GoogleAuthorizationCodeFlow flow, String refreshToken) throws IOException {
//        return flow.newTokenRequest(refreshToken).setGrantType("refresh_token").execute();
//    }
//
//    private Credential refreshAccessToken(Credential credential, int workspaceId) throws IOException {
//        TokenResponse response = refreshToken(googleAuthorizationCodeFlow, credential.getRefreshToken());
//        log.info("Access token refreshed successfully.");
//        credential.setAccessToken(response.getAccessToken());
//        credential.setExpiresInSeconds(response.getExpiresInSeconds());
//        // 수정: 새로운 토큰을 DB에 저장
//        // updateToken대신에 jpa로 했읍니다.
//        workspaceRepository.findById(workspaceId).get().setApiToken(response.getAccessToken());
//        return credential;
//    }

    public void getTokenExpiredTime(Credential credential) {
        log.info("Token Expired Time: {}", credential.getExpiresInSeconds());
    }
}
