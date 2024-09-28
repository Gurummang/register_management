package GASB.register_management.util;

import GASB.register_management.service.register.OrgSaasService;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class MsUtil {

    @Value("${spring.security.oauth2.client.registration.azure.client_id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.azure.client_secret}")
    private String clientSecret;  // 클라이언트 시크릿 추가

    @Value("${spring.security.oauth2.client.registration.azure.redirect_uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.azure.scope}")
    private String scope;

    private final OrgSaasService orgSaasService;

    @Autowired
    public MsUtil(OrgSaasService orgSaasService) {
        this.orgSaasService = orgSaasService;
    }

    public void func(String authCode) throws MalformedURLException, ExecutionException, InterruptedException, URISyntaxException {
        // Access Token 얻기
        String token = requestAccessTokenWithMSAL(authCode);
        log.info("Token: " + token);

        // OneDrive나 SharePoint의 드라이브 리스트 가져오기
        List<String[]> driveList = getDriveList(token);

        if(driveList.isEmpty()){
            log.info("No drive found");
        }

        // OrgSaasService를 통해 얻어온 리스트와 토큰을 전달하여 업데이트
        orgSaasService.updateOrgSaasMS(driveList, token);
    }

    private List<String[]> getDriveList(String accessToken) {
        // OneDrive API 엔드포인트 (필요에 따라 SharePoint 엔드포인트로 변경 가능)
        String endpoint = "https://graph.microsoft.com/v1.0/me/drives"; // OneDrive 사용자 드라이브 목록 엔드포인트

        // HTTP 요청을 위한 헤더 설정 (Bearer 토큰 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");

        // HTTP 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 드라이브 리스트를 가져오기 위한 GET 요청
        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

        // JSON 파싱 후 필요한 드라이브 ID와 이름을 추출
        String driveInfoJson = response.getBody();
        log.info("Drive List: " + driveInfoJson);
        List<String[]> driveList = parseDriveInfo(driveInfoJson); // 드라이브 정보 추출 로직은 별도로 구현 필요

        return driveList;
    }

    private List<String[]> parseDriveInfo(String json) {
        List<String[]> driveList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson의 ObjectMapper를 사용하여 JSON 파싱

        try {
            // JSON 문자열을 JsonNode로 변환
            JsonNode rootNode = objectMapper.readTree(json);
            // 'value' 배열에서 각 드라이브 정보를 추출
            JsonNode drivesArray = rootNode.path("value");

            // 각 드라이브 객체에서 id와 name을 추출하여 리스트에 추가
            for (JsonNode driveNode : drivesArray) {
                String id = driveNode.path("id").asText(); // 드라이브 id
                String name = driveNode.path("name").asText(); // 드라이브 name

                // "PersonalCacheLibrary"가 아닌 드라이브만 리스트에 추가
                if (!"PersonalCacheLibrary".equals(name)) {
                    driveList.add(new String[]{id, name});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error parsing drive information from JSON: {}", e.getMessage());
        }

        return driveList; // [id, name] 배열의 리스트 반환
    }


    private String requestAccessTokenWithMSAL(String authorizationCode) throws MalformedURLException, ExecutionException, InterruptedException, URISyntaxException {
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

        return result.accessToken();
    }
}
