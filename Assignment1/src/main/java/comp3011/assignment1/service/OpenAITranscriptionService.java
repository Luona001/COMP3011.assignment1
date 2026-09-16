package comp3011.assignment1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class for OpenAI speech-to-text integration.
 * 
 * Calls OpenAI's /v1/audio/transcriptions endpoint using Spring RestClient.
 * API key is loaded from environment variable OPENAI_API_KEY (never hardcoded).
 * Token usage (input/output tokens) is parsed from JSON response using Jackson ObjectMapper
 * and passed to ServerStatsService for cumulative tracking.
 * 
 * Uses RestClient instead of OpenAI Java SDK for simplicity and fewer dependencies.
 */
@Service
public class OpenAITranscriptionService {

    private final RestClient restClient;
    private final String apiKey;
    private final ServerStatsService statsService;

    public OpenAITranscriptionService(
            RestClient.Builder restClientBuilder,
            @Value("${openai.api.key}") String apiKey,
            ServerStatsService statsService) {

        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com")
                .build();

        this.apiKey = apiKey;
        this.statsService = statsService;
    }

    public String transcribe(MultipartFile audioFile)
            throws Exception {

        ByteArrayResource audioResource =
                new ByteArrayResource(audioFile.getBytes()) {
                    @Override
                    public String getFilename() {
                        return audioFile.getOriginalFilename();
                    }
                };

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add("file", audioResource);
        body.add("model", "gpt-4o-mini-transcribe");

        String response = restClient.post()
                .uri("/v1/audio/transcriptions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            var root = new ObjectMapper().readTree(response);
            long input = root.path("usage").path("input_tokens").asLong(0);
            long output = root.path("usage").path("output_tokens").asLong(0);
            statsService.addTokens(input, output);
        } catch (Exception e) {
        }
        return response;
    }
}