package comp3011.assignment1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OpenAITranscriptionService {

    private final RestClient restClient;
    private final String apiKey;

    public OpenAITranscriptionService(
            RestClient.Builder restClientBuilder,
            @Value("${openai.api.key}") String apiKey) {

        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com")
                .build();

        this.apiKey = apiKey;
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

        return restClient.post()
                .uri("/v1/audio/transcriptions")
                .header(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);
    }
}