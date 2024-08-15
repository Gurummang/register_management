package GASB.register_management.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class GoogleDriveConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_METADATA);

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws Exception {
        // Environment variables for Google API credentials
        Map<String, String> env = System.getenv();

        String clientId = Optional.ofNullable(env.get("CLIENT_ID")).orElseThrow(() -> new RuntimeException("CLIENT_ID not set"));
        String clientSecret = Optional.ofNullable(env.get("CLIENT_SECRET")).orElseThrow(() -> new RuntimeException("CLIENT_SECRET not set"));
        String authUri = Optional.ofNullable(env.get("AUTH_URI")).orElseThrow(() -> new RuntimeException("AUTH_URI not set"));
        String tokenUri = Optional.ofNullable(env.get("TOKEN_URI")).orElseThrow(() -> new RuntimeException("TOKEN_URI not set"));
        String authProviderX509CertUri = Optional.ofNullable(env.get("AUTH_PROVIDER_X509_CERT_URI")).orElseThrow(() -> new RuntimeException("AUTH_PROVIDER_X509_CERT_URI not set"));
        String projectId = Optional.ofNullable(env.get("PROJECT_ID")).orElseThrow(() -> new RuntimeException("PROJECT_ID not set"));
        String redirectUris = Optional.ofNullable(env.get("REDIRECT_URIS")).orElseThrow(() -> new RuntimeException("REDIRECT_URIS not set"));

        String jsonCredentials = String.format("{\"installed\":{\"client_id\":\"%s\",\"project_id\":\"%s\",\"auth_uri\":\"%s\",\"token_uri\":\"%s\",\"auth_provider_x509_cert_url\":\"%s\",\"client_secret\":\"%s\",\"redirect_uris\":[%s]}}",
                clientId, projectId, authUri, tokenUri, authProviderX509CertUri, clientSecret, redirectUris);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new StringReader(jsonCredentials)
        );

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
    }
}
