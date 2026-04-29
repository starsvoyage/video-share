package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.exception.GlobalExceptionHandler;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Ad;
import edu.arizona.videoshare.service.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdControllerTest {

    private MockMvc mockMvc;
    private AdService adService;

    @BeforeEach
    void setUp() {
        adService = mock(AdService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new AdController(adService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReturnsValidationMessagesForInvalidPayload() throws Exception {
        doThrow(new IllegalArgumentException("Ad title is required. Ad media URL is required. Ad duration must be 0 or greater."))
                .when(adService)
                .create(any(Ad.class));

        String payload = """
                {
                  "title": " ",
                  "mediaUrl": "",
                  "duration": -1,
                  "active": true
                }
                """;

        mockMvc.perform(post("/api/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ad title is required. Ad media URL is required. Ad duration must be 0 or greater."))
                .andExpect(content().string(containsString("Ad title is required.")))
                .andExpect(content().string(containsString("Ad media URL is required.")))
                .andExpect(content().string(containsString("Ad duration must be 0 or greater.")));
    }

    @Test
    void createReturnsClearMessageForInvalidEnumValues() throws Exception {
        String payload = """
                {
                  "title": "Pre-roll ad",
                  "mediaUrl": "https://example.com/ad.mp4",
                  "duration": 15,
                  "active": true,
                  "placement": "SIDEWAYS",
                  "startAt": "2026-04-28T10:00:00",
                  "endAt": "2026-04-28T10:15:00"
                }
                """;

        mockMvc.perform(post("/api/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is invalid. Check field names, formats, and enum values."));
    }

    @Test
    void createReturnsClearMessageForInvalidSchedule() throws Exception {
        doThrow(new IllegalArgumentException("Ad end time must be after the start time."))
                .when(adService)
                .create(any(Ad.class));

        String payload = """
                {
                  "title": "Pre-roll ad",
                  "mediaUrl": "https://example.com/ad.mp4",
                  "duration": 15,
                  "active": true,
                  "placement": "Pre_roll",
                  "startAt": "2026-04-28T10:15:00",
                  "endAt": "2026-04-28T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ad end time must be after the start time."));
    }

    @Test
    void deleteReturnsNotFoundMessageWhenAdDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Ad not found: 999999"))
                .when(adService)
                .delete(999999L);

        mockMvc.perform(delete("/api/ads/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ad not found: 999999"));
    }
}
