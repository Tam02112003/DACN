package DACN.DACN.services;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class GroqAIService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GroqAIService.class);
    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    public String chatWithAI(String userMessage) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);

            // Headers
            request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");

            // Request body cho GroqCloud
            String jsonBody = String.format(
                    "{\"model\":\"deepseek-r1-distill-llama-70b\"," +
                            "\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]," +
                            "\"temperature\":0.7," +
                            "\"max_tokens\":1024}",
                    userMessage.replace("\"", "\\\""));

            request.setEntity(new StringEntity(jsonBody));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Groq API Response: " + responseBody); // Debug log

                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        }
    }
}