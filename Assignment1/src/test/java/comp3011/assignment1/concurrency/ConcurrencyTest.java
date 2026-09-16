package comp3011.assignment1.concurrency;

import comp3011.assignment1.service.ServerStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "openai.api.key=test-key")
class ConcurrencyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ServerStatsService statsService;

    @Test
    void concurrentUptimeRequests_noCrash() throws Exception {
        int threadCount = 250;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    var response = restTemplate.getForEntity("/api/v1/admin/uptime", String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(successCount.get() > 0, "At least some requests should succeed");
        assertEquals(0, failCount.get(), "No requests should fail");
    }

    @Test
    void concurrentAddTokens_noRaceCondition() throws Exception {
        int threadCount = 200;
        long tokensPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    statsService.addTokens(tokensPerThread, tokensPerThread);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long expected = (long) threadCount * tokensPerThread;
        assertEquals(expected, statsService.getInputTokens(),
                "Input tokens should match total from all threads");
        assertEquals(expected, statsService.getOutputTokens(),
                "Output tokens should match total from all threads");
    }
}