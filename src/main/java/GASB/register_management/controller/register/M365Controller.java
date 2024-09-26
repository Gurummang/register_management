package GASB.register_management.controller.register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

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
    public String getLoginUri() throws NoSuchAlgorithmException {
        // code_verifier와 code_challenge 생성
        codeVerifier = generateCodeVerifier();  // code_verifier 생성
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
            @RequestParam("state") String state) {

        log.info("Received Authorization Code: {}", authorizationCode);
        log.info("Received state: {}", state);

        // Access Token 요청을 위한 로직 추가
        String accessToken = requestAccessToken(authorizationCode);
        log.info("Access Token: {}", accessToken);

        return "Access Token received successfully";
    }

    // Authorization Code로 Access Token을 요청하는 메서드
    private String requestAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 파라미터 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);  // 클라이언트 시크릿 추가
        requestBody.add("code", authorizationCode);  // 받은 Authorization Code
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code_verifier", codeVerifier);  // PKCE code_verifier 추가

        // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        // 토큰 엔드포인트로 POST 요청
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        // 응답에서 Access Token 추출 (JSON 파싱 후 Access Token을 추출 가능)
        return response.getBody();  // 실제로는 JSON 파싱 후 Access Token을 추출해야 함
    }

    // PKCE code_verifier 생성
    private String generateCodeVerifier() {
        return UUID.randomUUID().toString();  // PKCE에서 사용할 랜덤 문자열 생성
    }

    // PKCE code_challenge 생성 (code_verifier의 SHA256 해시값)
    private String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
