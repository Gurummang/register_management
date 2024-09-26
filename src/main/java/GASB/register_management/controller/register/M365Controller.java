package GASB.register_management.controller.register;

import com.microsoft.aad.msal4j.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@RequestMapping("/api/v1/org-saas/azure")
public class M365Controller {

    @Value("${spring.security.oauth2.client.registration.azure.client_id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.azure.client_secret}")
    private String clientSecret;  // 클라이언트 시크릿 추가

    @Value("${spring.security.oauth2.client.registration.azure.redirect_uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.azure.scope}")
    private String scope;

    private final String authorityUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";
    private final String tokenUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/token";  // 토큰 요청 URL

    // PKCE에 사용되는 code_verifier를 저장하는 필드 (임시로 관리)
    private String codeVerifier;

    // 1. 사용자가 접근해야 할 login_uri 생성
    @GetMapping("/getUrl")
    public String getLoginUri() throws Exception {
        log.info("Here");
        codeVerifier = generateCodeVerifier();  // code_verifier 생성
        log.info("codeVerifier: {}", codeVerifier);
        String codeChallenge = generateCodeChallenge(codeVerifier);  // code_verifier로부터 code_challenge 생성

        String loginUri = UriComponentsBuilder.fromHttpUrl(authorityUrl)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_mode", "query")
                .queryParam("scope", scope)
                .queryParam("state", "12345")  // CSRF 방지를 위한 state 값
                .queryParam("code_challenge", codeChallenge)  // PKCE code_challenge
                .queryParam("code_challenge_method", "S256")  // PKCE 메서드 지정
                .toUriString();

        log.info("Generated login_uri: {}", loginUri);
        return loginUri;
    }

    // 2. 토큰을 처리할 redirect_uri (Authorization Code를 수신)
    @GetMapping("/token")
    public String handleAuthorizationCode(
            @RequestParam("code") String authorizationCode,
            @RequestParam("state") String state) throws Exception {

        log.info("Received Authorization Code: {}", authorizationCode);
        log.info("Received state: {}", state);

        // Access Token 요청을 위한 MSAL 라이브러리 사용
        String accessToken = requestAccessTokenWithMSAL(authorizationCode);
        log.info("Access Token: {}", accessToken);

        return "Access Token received successfully";
    }

    // MSAL을 사용하여 Authorization Code로 Access Token을 요청하는 메서드
    private String requestAccessTokenWithMSAL(String authorizationCode) throws Exception {
        // MSAL 라이브러리 사용
        ConfidentialClientApplication app = ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret))
                .authority("https://login.microsoftonline.com/common/")  // 인증 서버 URL
                .build();

        Set<String> scopes = Collections.singleton(scope);

        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(authorizationCode, new URI(redirectUri))
                .scopes(scopes)
                .codeVerifier(codeVerifier)  // PKCE code_verifier 추가
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        IAuthenticationResult result = future.get();

        // Access Token 반환
        return result.accessToken();
    }

    // PKCE code_verifier 생성
    private String generateCodeVerifier() {
        // 두 개의 UUID를 결합하여 43자 이상의 랜덤 문자열 생성
        String verifier = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");

        // 필요 시 43자 이상으로 자르고 반환 (필요한 만큼만 자르도록 처리)
        return verifier.length() >= 64 ? verifier.substring(0, 64) : verifier;
    }


    // PKCE code_challenge 생성 (code_verifier의 SHA256 해시값)
    private String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
