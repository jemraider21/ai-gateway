package com.aigateway.llm;

import com.aigateway.llm.request.impl.RequestBody;
import com.aigateway.llm.request.impl.RequestMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class LmStudioClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String model;

    public LmStudioClient(String baseUrl, String model) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public String generateResponse(String prompt) throws IOException, InterruptedException {
        RequestMessage message = new RequestMessage(prompt);
        RequestBody requestBody = new RequestBody(model, List.of(message), false);
        String jsonBody = objectMapper.writeValueAsString(requestBody.toMap());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(5))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200) {
            throw new IOException(String.format("LM Studio returned HTTP %s: %s", response.statusCode(), response.body()));
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode choices = root.path("choices");
        if(!choices.isArray() || choices.isEmpty()) {
            throw new IOException("LM Studio returned no response choices");
        }

        JsonNode content = choices.get(0).path("message").path("content");
        if(!content.isTextual()) {
            throw new IOException("LM Studio returned no text content");
        }

        return content.asText();
    }
}
