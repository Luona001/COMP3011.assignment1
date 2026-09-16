package comp3011.assignment1.controller;

import comp3011.assignment1.service.OpenAITranscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for audio transcription.
 * 
 * Endpoint: POST /api/v1/transcribe
 * - Accepts multipart/form-data audio file upload (parameter name: "audio")
 * - Forwards audio to OpenAITranscriptionService for processing
 * - Returns raw JSON response from OpenAI (includes "text" and "usage" fields)
 * - Returns HTTP 500 with error message if transcription fails
 */
@RestController
@RequestMapping("/api/v1")
public class TranscriptionController {
	private static final Logger log = LoggerFactory.getLogger(TranscriptionController.class);
	private final OpenAITranscriptionService transcriptionService;

    public TranscriptionController(
            OpenAITranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }
    
    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(
            @RequestParam("audio") MultipartFile audio) {

        try {
            String result = transcriptionService.transcribe(audio);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
        	log.error("Transcription failed: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Transcription failed: " + e.getMessage());
        }
    }
}
