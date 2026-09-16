package comp3011.assignment1.service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe service for tracking server statistics.
 * 
 * Tracks cumulative input/output token usage across all transcription calls.
 * Uses AtomicLong for concurrent access without explicit locking, which is
 * essential when virtual threads call addTokens() simultaneously.
 * Also manages server start time (for uptime calculation) and shutdown state.
 */
@Service
public class ServerStatsService {

    private final Instant serverStartTime;
    private final AtomicLong inputTokens;
    private final AtomicLong outputTokens;
    private final AtomicBoolean shutdownInProgress;

    public ServerStatsService() {
        this.serverStartTime = Instant.now();
        this.inputTokens = new AtomicLong(0);
        this.outputTokens = new AtomicLong(0);
        this.shutdownInProgress = new AtomicBoolean(false);
    }

    public Instant getServerStartTime() {
        return serverStartTime;
    }

    public long getInputTokens() {
        return inputTokens.get();
    }

    public long getOutputTokens() {
        return outputTokens.get();
    }

    public void addTokens(long input, long output) {
        inputTokens.addAndGet(input);
        outputTokens.addAndGet(output);
    }

    public boolean isShutdownInProgress() {
        return shutdownInProgress.get();
    }

    public boolean tryStartShutdown() {
        return shutdownInProgress.compareAndSet(false, true);
    }
}