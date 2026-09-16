package comp3011.assignment1.controller;

import comp3011.assignment1.service.ServerStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.ConfigurableApplicationContext;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for server administration.
 * 
 * Endpoints:
 * - GET /api/v1/admin/uptime
 *   Returns JSON with "utcServerStart", "utcNow", and "serverUptimeSeconds"
 * 
 * - POST /api/v1/admin/shutdown
 *   Initiates graceful shutdown after 1 second delay.
 *   Returns 202 Accepted on first call, 409 Conflict if already in progress.
 *   Uses AtomicBoolean to prevent duplicate shutdown requests.
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final ServerStatsService statsService;
    private final ConfigurableApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    public AdminController(
            ServerStatsService statsService,
            ConfigurableApplicationContext applicationContext) {
        this.statsService = statsService;
        this.applicationContext = applicationContext;
    }

    @GetMapping("/uptime")
    public ResponseEntity<Map<String, Object>> getUptime() {
        Instant now = Instant.now();
        Instant start = statsService.getServerStartTime();
        double uptimeSeconds = Duration.between(start, now).toMillis() / 1000.0;

        return ResponseEntity.ok(Map.of(
                "utcServerStart", start.toString(),
                "utcNow", now.toString(),
                "serverUptimeSeconds", uptimeSeconds
        ));
    }

    @PostMapping("/shutdown")
    public ResponseEntity<Map<String, Object>> shutdown() {
        if (!statsService.tryStartShutdown()) {
        	log.info("Shutdown already in progress, returning 409");
            return ResponseEntity.status(409).body(Map.of(
                    "timestamp", Instant.now().toString(),
                    "status", 409,
                    "error", "Conflict",
                    "message", "Graceful shutdown is already in progress.",
                    "path", "/api/v1/admin/shutdown"
            ));
        }

        log.info("Graceful shutdown requested");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            applicationContext.close();
        }).start();

        return ResponseEntity.accepted().body(Map.of(
                "message", "Graceful shutdown requested."
        ));
    }
}