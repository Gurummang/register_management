package GASB.register_management.util;

import GASB.register_management.service.register.OrgSaasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;


@Service
@Slf4j
public class MsUtil {

    @Value("${spring.security.oauth2.client.registration.azure.client_id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.azure.client_secret}")
    private String clientSecret;  // 클라이언트 시크릿 추가

    @Value("${spring.security.oauth2.client.registration.azure.redirect_uri}")
    private String redirectUri;

    private final OrgSaasService orgSaasService;

    @Autowired
    public MsUtil(OrgSaasService orgSaasService) {
        this.orgSaasService = orgSaasService;
    }

    public void func(String authCode) {
        Map<String, String> tokens = requestTokensWithAuthCode(authCode);
        String accessToken = tokens.get("access_token");
        String refreshToken = tokens.get("refresh_token");

        // OneDrive나 SharePoint의 드라이브 리스트 가져오기
        List<String[]> driveList = getDriveList(accessToken);

        // OrgSaasService를 통해 얻어온 리스트와 토큰을 전달하여 업데이트
        if (driveList != null && accessToken != null && refreshToken != null) {
            orgSaasService.updateOrgSaasMS(driveList, accessToken, refreshToken);
        } else {
            log.warn("One or more required parameters are null. Skipping updateOrgSaasMS call.");
        }
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

        return parseDriveInfo(driveInfoJson);
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
            log.error("Error parsing drive information from JSON: {}", e.getMessage());
        }

        return driveList; // [id, name] 배열의 리스트 반환
    }


    public Map<String, String> requestTokensWithAuthCode(String authCode) {
        String tokenUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", authCode); // Authorization Code
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");
        body.add("scope", "https://graph.microsoft.com/.default offline_access");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);

        Map<String, String> tokens = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            tokens.put("access_token", jsonNode.get("access_token").asText());
            if (jsonNode.has("refresh_token")) {
                tokens.put("refresh_token", jsonNode.get("refresh_token").asText());
            }
        } catch (IOException e) {
            log.error("Error parsing the token response: {}", e.getMessage());
        }

        return tokens; // Access Token과 Refresh Token 반환
    }
}
