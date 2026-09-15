package comp3011.assignment1.controller;

import comp3011.assignment1.service.ServerStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatsController {

    private final ServerStatsService statsService;

    public GlobalStatsController(ServerStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        return ResponseEntity.ok(Map.of(
                "inputTokens", statsService.getInputTokens(),
                "outputTokens", statsService.getOutputTokens()
        ));
    }
}