package GASB.register_management.controller.register;

import com.microsoft.aad.msal4j.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    // 1. 사용자가 접근해야 할 login_uri 생성
    @GetMapping("/getUrl")
    public String getLoginUri() {
        log.info("Here");

        // 스코프가 2개 이상일 때, 구분자 변경
        String formattedScope = scope.replace(",", " ");

        String loginUri = UriComponentsBuilder.fromHttpUrl(authorityUrl)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_mode", "query")
                .queryParam("scope", formattedScope)
                .queryParam("state", "12345")  // CSRF 방지를 위한 state 값
                .queryParam("prompt", "consent")  // 사용자 동의를 다시 요청
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

//    // M365에서 사용자 목록을 가져오는 메서드 (POST로 변경)
//    @PostMapping("/getUsers")
//    public String getM365Users(@RequestBody Map<String, String> body) throws Exception {
//        String accessToken = body.get("accessToken");
//
//        // Microsoft Graph API 엔드포인트
//        String usersEndpoint = "https://graph.microsoft.com/v1.0/users";
//
//        // HTTP 요청을 위한 헤더 설정 (Bearer 토큰 포함)
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        headers.set("Accept", "application/json");
//
//        // HTTP 요청 보내기
//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        // 사용자 목록을 가져오기 위한 GET 요청
//        ResponseEntity<String> response = restTemplate.exchange(usersEndpoint, HttpMethod.GET, entity, String.class);
//
//        // 반환된 JSON 사용자 목록
//        String usersJson = response.getBody();
//        log.info("User List: " + usersJson);
//
//        return usersJson;  // 사용자 목록을 반환하거나 원하는 대로 처리
//    }

    // MSAL을 사용하여 Authorization Code로 Access Token을 요청하는 메서드
    private String requestAccessTokenWithMSAL(String authorizationCode) throws Exception {
        // MSAL 라이브러리 사용
        ConfidentialClientApplication app = ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret))
                .authority("https://login.microsoftonline.com/common/")  // 인증 서버 URL
                .build();

        // 스코프 처리: 스코프가 ','로 구분되어 있는 경우, 공백으로 구분된 형태로 변환
        String formattedScope = scope.replace(",", " ");

        // 스코프들을 공백으로 나눠 Set<String>에 추가
        Set<String> scopes = new HashSet<>(Arrays.asList(formattedScope.split(" ")));

        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(authorizationCode, new URI(redirectUri))
                .scopes(scopes)
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        IAuthenticationResult result = future.get();

        // Access Token 반환
        return result.accessToken();
    }
}
