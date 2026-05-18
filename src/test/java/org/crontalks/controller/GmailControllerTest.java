package org.crontalks.controller;

import org.crontalks.service.GmailService;
import org.crontalks.service.GmailSmtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GmailControllerTest {

    private MockMvc mockMvc;

    @Mock private GmailService gmailService;
    @Mock private GmailSmtpService gmailSmtpService;

    @InjectMocks
    private GmailController gmailController;

    private static final String EMAIL_ENDPOINT = "/email";
    private static final String CURRENT_SPEAKER_ENDPOINT = "/email/speaker/current";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_BODY = "Test Body";
    private static final String SUCCESS_RESPONSE = "Email sent successfully";
    private static final String ERROR_RESPONSE = "Error sending email";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gmailController).build();
    }

    @Test
    void sendEmail_ShouldReturnSuccessResponse_WhenEmailIsSentSuccessfully() throws Exception {
        doNothing().when(gmailSmtpService).sendEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post(EMAIL_ENDPOINT)
                        .param("to", TEST_EMAIL)
                        .param("subject", TEST_SUBJECT)
                        .param("body", TEST_BODY)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(gmailSmtpService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendEmail_ShouldReturnErrorResponse_WhenServiceThrowsException() throws Exception {
        doThrow(new RuntimeException(ERROR_RESPONSE)).when(gmailSmtpService).sendEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post(EMAIL_ENDPOINT)
                        .param("to", TEST_EMAIL)
                        .param("subject", TEST_SUBJECT)
                        .param("body", TEST_BODY)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(ERROR_RESPONSE));

        verify(gmailSmtpService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendEmailCurrent_ShouldReturnSuccessResponse_WhenEmailIsSentSuccessfully() throws Exception {
        doReturn(SUCCESS_RESPONSE).when(gmailService).sendMailCurrent();

        mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESS_RESPONSE));

        verify(gmailService).sendMailCurrent();
    }

    @Test
    void sendEmailCurrent_ShouldReturnErrorResponse_WhenServiceThrowsException() throws Exception {
        doThrow(new RuntimeException(ERROR_RESPONSE)).when(gmailService).sendMailCurrent();

        mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(ERROR_RESPONSE));

        verify(gmailService).sendMailCurrent();
    }

    @Test
    void sendEmailCurrent_ShouldPropagateMessagingException() throws Exception {
        RuntimeException messagingException = new RuntimeException("Messaging error");
        when(gmailService.sendMailCurrent()).thenThrow(messagingException);

        mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Messaging error"));

        verify(gmailService).sendMailCurrent();
    }

    @Test
    void sendEmailCurrent_ShouldPropagateUnsupportedEncodingException() throws Exception {
        RuntimeException encodingException = new RuntimeException("Encoding error");
        when(gmailService.sendMailCurrent()).thenThrow(encodingException);

        mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Encoding error"));

        verify(gmailService).sendMailCurrent();
    }
}
