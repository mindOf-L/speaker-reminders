package org.crontalks.controller;

import org.crontalks.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TestService testService;

    @InjectMocks
    private TestController testController;

    private static final String EMAIL_TEST_ENDPOINT = "/test/email";
    private static final String WHATSAPP_TEST_ENDPOINT = "/test/whatsapp";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_RESPONSE = "Test response";
    private static final String[] TEST_CC = new String[]{"cc1@example.com", "cc2@example.com"};

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void getMailTest_ShouldReturnEmailTemplate() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(TEST_RESPONSE, HttpStatus.OK);
        doReturn(response).when(testService).getMailTest();

        mockMvc.perform(get(EMAIL_TEST_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_RESPONSE));

        verify(testService).getMailTest();
    }

    @Test
    void sendMailTest_WithAllParameters_ShouldSendEmail() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(TEST_RESPONSE, HttpStatus.OK);
        doReturn(response).when(testService).sendMailTest(anyString(), anyString(), any());

        mockMvc.perform(post(EMAIL_TEST_ENDPOINT)
                        .param("to", TEST_EMAIL)
                        .param("subject", TEST_SUBJECT)
                        .param("cc", TEST_CC[0], TEST_CC[1])
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_RESPONSE));

        verify(testService).sendMailTest(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_CC));
    }

    @Test
    void sendMailTest_WithoutToParameter_ShouldUseDefaultEmail() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(TEST_RESPONSE, HttpStatus.OK);
        doReturn(response).when(testService).sendMailTest(isNull(), anyString(), any());

        mockMvc.perform(post(EMAIL_TEST_ENDPOINT)
                        .param("subject", TEST_SUBJECT)
                        .param("cc", TEST_CC[0], TEST_CC[1])
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_RESPONSE));

        verify(testService).sendMailTest(isNull(), eq(TEST_SUBJECT), eq(TEST_CC));
    }

    @Test
    void sendMailTest_WithoutCcParameter_ShouldSendWithoutCc() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(TEST_RESPONSE, HttpStatus.OK);
        doReturn(response).when(testService).sendMailTest(anyString(), anyString(), isNull());

        mockMvc.perform(post(EMAIL_TEST_ENDPOINT)
                        .param("to", TEST_EMAIL)
                        .param("subject", TEST_SUBJECT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_RESPONSE));

        verify(testService).sendMailTest(eq(TEST_EMAIL), eq(TEST_SUBJECT), isNull());
    }

    @Test
    void getWhatsAppTest_ShouldReturnWhatsAppTemplate() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(TEST_RESPONSE, HttpStatus.OK);
        doReturn(response).when(testService).getWhatsAppTest();

        mockMvc.perform(get(WHATSAPP_TEST_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_RESPONSE));

        verify(testService).getWhatsAppTest();
    }

    @Test
    void sendWhatsAppTest_ShouldSendWhatsAppMessage() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(TEST_RESPONSE, HttpStatus.CREATED);
        doReturn(response).when(testService).sendWhatsAppTest();

        mockMvc.perform(post(WHATSAPP_TEST_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(TEST_RESPONSE));

        verify(testService).sendWhatsAppTest();
    }
}
