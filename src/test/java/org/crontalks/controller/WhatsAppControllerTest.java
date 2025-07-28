package org.crontalks.controller;

import org.crontalks.constants.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WhatsAppControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private WhatsAppController whatsAppController;

    private static final String WHATSAPP_SPEAKER_CURRENT_ENDPOINT = "/whatsapp/speaker/current";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(whatsAppController).build();
    }

    @Test
    void sendWhatsAppSpeakerCurrent_ShouldReturnNotImplementedYet() throws Exception {
        mockMvc.perform(post(WHATSAPP_SPEAKER_CURRENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotImplemented())
                .andExpect(content().string(Messages.NOT_IMPLEMENTED_YET));
    }
}
