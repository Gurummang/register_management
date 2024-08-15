package GASB.register_management.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
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
import java.util.concurrent.*;

@Service
@Slf4j
public class GoogleUtil {

    private static final String APPLICATION_NAME = "grummang-google-drive-func";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private static final int AUTH_TIMEOUT = 2; // 타임아웃 시간 (분)

    @Autowired
    public GoogleUtil(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
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

    public Credential getCredentials() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8088)
                .setCallbackPath("/login/oauth2/code/google")
                .build();

        Callable<Credential> task = () -> new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, receiver).authorize("user");

        Future<Credential> future = executorService.submit(task);
        try {
            // 주어진 시간(1분) 안에 인증을 완료해야 함
            return future.get(AUTH_TIMEOUT, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            future.cancel(true); // 인증 작업 취소
            log.error("Authentication request timed out after {} minutes", AUTH_TIMEOUT);
            throw new RuntimeException("Authentication timed out.");
        } catch (ExecutionException | InterruptedException e) {
            log.error("An error occurred during authentication: {}", e.getMessage());
            throw new RuntimeException("Authentication failed.", e);
        } finally {
            executorService.shutdown(); // Executor 서비스 종료
        }
    }
}
