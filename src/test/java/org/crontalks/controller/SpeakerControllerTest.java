package org.crontalks.controller;

import org.crontalks.constants.Messages;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.service.SpeakerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SpeakerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SpeakerService speakerService;

    @InjectMocks
    private SpeakerController speakerController;

    private static final String SPEAKER_ENDPOINT = "/speaker";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(speakerController).build();
    }

    @Test
    void speaker_ShouldReturnScheduledTalk_WhenDataIsAvailable() throws Exception {
        LocalDateTime talkDateTime = LocalDateTime.of(2025, 7, 30, 19, 0);
        ScheduledTalk scheduledTalk = ScheduledTalk.builder()
                .localDateTime(talkDateTime)
                .name("John Doe")
                .congregation("Test Congregation")
                .outlineNumber(123)
                .outlineTitle("Test Talk")
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .outlineHasImages(true)
                .build();

        when(speakerService.getCurrentScheduledTalk()).thenReturn(scheduledTalk);

        mockMvc.perform(get(SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.congregation").value("Test Congregation"))
                .andExpect(jsonPath("$.outlineNumber").value(123))
                .andExpect(jsonPath("$.outlineTitle").value("Test Talk"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.outlineHasImages").value(true));
    }

    @Test
    void speaker_ShouldReturnError_WhenNoDataIsAvailable() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);

        mockMvc.perform(get(SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(Messages.ERROR_GETTING_DATA_FROM_GSHEET));
    }
}
