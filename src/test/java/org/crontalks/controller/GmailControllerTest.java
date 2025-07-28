package org.crontalks.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import org.crontalks.service.GmailService;
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

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GmailControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GmailService gmailService;
    
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
    void sendMail_ShouldReturnSuccessResponse_WhenEmailIsSentSuccessfully() throws Exception {
        ResponseEntity<String> successResponse = ResponseEntity.ok(SUCCESS_RESPONSE);
        doReturn(successResponse).when(gmailService).sendMail(anyString(), anyString(), anyString());

        mockMvc.perform(post(EMAIL_ENDPOINT)
                        .param("to", TEST_EMAIL)
                        .param("subject", TEST_SUBJECT)
                        .param("body", TEST_BODY)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESS_RESPONSE));

        verify(gmailService).sendMail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendMail_ShouldReturnErrorResponse_WhenServiceThrowsException() throws Exception {
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERROR_RESPONSE);
        doReturn(errorResponse).when(gmailService).sendMail(anyString(), anyString(), anyString());

        mockMvc.perform(post(EMAIL_ENDPOINT)
                        .param("to", TEST_EMAIL)
                        .param("subject", TEST_SUBJECT)
                        .param("body", TEST_BODY)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(ERROR_RESPONSE));

        verify(gmailService).sendMail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendMailCurrent_ShouldReturnSuccessResponse_WhenEmailIsSentSuccessfully() throws Exception {
        ResponseEntity<String> successResponse = ResponseEntity.ok(SUCCESS_RESPONSE);
        doReturn(successResponse).when(gmailService).sendMailCurrent();

        mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESS_RESPONSE));

        verify(gmailService).sendMailCurrent();
    }

    @Test
    void sendMailCurrent_ShouldReturnErrorResponse_WhenServiceThrowsException() throws Exception {
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERROR_RESPONSE);
        doReturn(errorResponse).when(gmailService).sendMailCurrent();

        mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(ERROR_RESPONSE));

        verify(gmailService).sendMailCurrent();
    }

    @Test
    void sendMailCurrent_ShouldPropagateMessagingException() throws Exception {
        MessagingException messagingException = new MessagingException("Messaging error");
        when(gmailService.sendMailCurrent()).thenThrow(messagingException);

        try {
            mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED));
            org.junit.jupiter.api.Assertions.fail("Expected ServletException was not thrown");
        } catch (ServletException e) {
            assertInstanceOf(MessagingException.class, e.getCause());
        }

        verify(gmailService).sendMailCurrent();
    }

    @Test
    void sendMailCurrent_ShouldPropagateUnsupportedEncodingException() throws Exception {
        UnsupportedEncodingException encodingException = new UnsupportedEncodingException("Encoding error");
        when(gmailService.sendMailCurrent()).thenThrow(encodingException);

        try {
            mockMvc.perform(post(CURRENT_SPEAKER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED));
            org.junit.jupiter.api.Assertions.fail("Expected UnsupportedEncodingException was not thrown");
        } catch (UnsupportedEncodingException e) {
            assertEquals("Encoding error", e.getMessage());
        }

        verify(gmailService).sendMailCurrent();
    }
}
