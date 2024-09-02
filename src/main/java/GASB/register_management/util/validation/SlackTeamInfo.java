package GASB.register_management.util.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SlackTeamInfo {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getTeamInfo(String token) throws IOException, InterruptedException {
        String url = "https://slack.com/api/team.info";

        // HttpClient 생성
        HttpClient client = HttpClient.newHttpClient();

        // 요청 준비
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        // 요청 보내기 및 응답 받기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 출력 및 필요한 값 추출
        if (response.statusCode() == 200) {
            String responseBody = response.body();

            // JSON 응답을 JsonNode로 변환
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // JSON 응답에서 필요한 값 추출
            if (jsonNode.path("ok").asBoolean()) {
                JsonNode teamNode = jsonNode.path("team");

                String teamName = Optional.ofNullable(teamNode.path("name").asText()).filter(s -> !s.isEmpty()).orElse("");
                String teamId = Optional.ofNullable(teamNode.path("id").asText()).filter(s -> !s.isEmpty()).orElse("");

                List<String> teamInfo = new ArrayList<>();
                teamInfo.add(teamName);
                teamInfo.add(teamId);

                // 특정 값 반환
                return teamInfo;
            } else {
                String error = Optional.ofNullable(jsonNode.path("error").asText()).filter(s -> !s.isEmpty()).orElse("Unknown error");
                throw new IOException("Error: " + error);
            }
        } else {
            throw new IOException("Failed to fetch team info. Status code: " + response.statusCode());
        }
    }
}