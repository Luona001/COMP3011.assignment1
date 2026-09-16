package comp3011.assignment1.controller;

import comp3011.assignment1.service.ServerStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServerStatsService statsService;

    @MockitoBean
    private ConfigurableApplicationContext applicationContext;

    @Test
    void uptime_returnsUptimeJson() throws Exception {
        Mockito.when(statsService.getServerStartTime()).thenReturn(Instant.now());

        mockMvc.perform(get("/api/v1/admin/uptime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.utcServerStart").exists())
                .andExpect(jsonPath("$.utcNow").exists())
                .andExpect(jsonPath("$.serverUptimeSeconds").isNumber());
    }

    @Test
    void shutdown_firstCall_returns202() throws Exception {
        Mockito.when(statsService.tryStartShutdown()).thenReturn(true);

        mockMvc.perform(post("/api/v1/admin/shutdown"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Graceful shutdown requested."));
    }

    @Test
    void shutdown_secondCall_returns409() throws Exception {
        Mockito.when(statsService.tryStartShutdown()).thenReturn(true).thenReturn(false);

        mockMvc.perform(post("/api/v1/admin/shutdown"));
        mockMvc.perform(post("/api/v1/admin/shutdown"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}