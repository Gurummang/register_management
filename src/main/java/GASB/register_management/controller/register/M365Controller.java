package GASB.register_management.controller.register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
