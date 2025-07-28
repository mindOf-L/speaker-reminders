package org.crontalks.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AliveControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AliveController aliveController;

    private static final String ALIVE_ENDPOINT = "/alive";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aliveController).build();
    }

    @Test
    void getAlive_ShouldReturnAliveMessage_WithStatusOk() throws Exception {
        mockMvc.perform(get(ALIVE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("I'm alive!")));
    }
}
