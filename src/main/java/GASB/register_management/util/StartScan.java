package GASB.register_management.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class StartScan {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode postToScan(String spaceId, String adminEmail, String saasName) throws IOException, InterruptedException {
        String url = "https://back.grummang.com/api/v1/connect/"+saasName+"/all";

        System.out.println("Enter Fucntion");
        System.out.println(url);
        // HttpClient 생성
        HttpClient client = HttpClient.newHttpClient();

        // 요청 본문 준비
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("spaceId", spaceId);
        requestBodyMap.put("email", adminEmail);
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        System.out.println("RequestBody" + requestBody);

        // 요청 준비
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("Request: " + request + "\n");

        System.out.println(" -- 3 -- ");
        System.out.println("Post to API");

        // 요청 보내기 및 응답 받기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response" + response);
        // 응답 출력 및 필요한 값 추출
        if (response.statusCode() == 200) {
            String responseBody = response.body();

            // JSON 응답을 JsonNode로 변환
            System.out.println("Resp Body: " + responseBody);
            return objectMapper.readTree(responseBody);
        } else {
            System.out.println("Error: " + response.statusCode() + " " + response.body());
            throw new IOException("Fail" + response.statusCode());
        }
    }
}
