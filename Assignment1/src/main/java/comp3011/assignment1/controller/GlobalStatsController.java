package comp3011.assignment1.controller;

import comp3011.assignment1.service.ServerStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for global token usage statistics.
 * 
 * Endpoint: GET /api/v1/global/stats
 * - Returns JSON with "inputTokens" and "outputTokens" fields
 * - Values are cumulative across all transcription calls since server start
 * - Used by frontend and TITAN testing to verify token tracking accuracy
 */
@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatsController {
	private static final Logger log = LoggerFactory.getLogger(GlobalStatsController.class);
    private final ServerStatsService statsService;

    public GlobalStatsController(ServerStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
    	log.info("Global stats requested: inputTokens={}, outputTokens={}",
                statsService.getInputTokens(), statsService.getOutputTokens());
        return ResponseEntity.ok(Map.of(
                "inputTokens", statsService.getInputTokens(),
                "outputTokens", statsService.getOutputTokens()
        ));
    }
}