package comp3011.assignment1.controller;

import comp3011.assignment1.service.OpenAITranscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TranscriptionController.class)
class TranscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpenAITranscriptionService transcriptionService;

    @Test
    void transcribe_returnsJsonText() throws Exception {
        when(transcriptionService.transcribe(any()))
                .thenReturn("{\"text\":\"Hello world\",\"usage\":{\"input_tokens\":10,\"output_tokens\":5}}");

        MockMultipartFile audio = new MockMultipartFile(
                "audio", "test.webm", "audio/webm", "fake audio data".getBytes());

        mockMvc.perform(multipart("/api/v1/transcribe").file(audio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Hello world"));
    }

    @Test
    void transcribe_serviceThrows_returns500() throws Exception {
        when(transcriptionService.transcribe(any()))
                .thenThrow(new RuntimeException("API error"));

        MockMultipartFile audio = new MockMultipartFile(
                "audio", "test.webm", "audio/webm", "fake audio data".getBytes());

        mockMvc.perform(multipart("/api/v1/transcribe").file(audio))
                .andExpect(status().isInternalServerError());
    }
}