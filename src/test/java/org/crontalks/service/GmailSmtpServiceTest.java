package org.crontalks.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.crontalks.constants.Messages.ERROR_EMAIL_RECIPIENT_NOT_INFORMED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GmailSmtpServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SchedulingProperties schedulingProperties;

    @InjectMocks
    private GmailSmtpService gmailSmtpService;

    private MimeMessage mockMimeMessage;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_BODY = "Test Body";
    private static final String EMAIL_FROM = "from@example.com";
    private static final String TALKS_OVERSEER = "Test Overseer";
    private static final String[] EMAIL_CC = new String[]{"cc@example.com"};

    @BeforeEach
    void setUp() {
        mockMimeMessage = mock(MimeMessage.class);
        lenient().when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        lenient().when(schedulingProperties.getEmailFrom()).thenReturn(EMAIL_FROM);
        lenient().when(schedulingProperties.getTalksOverseer()).thenReturn(TALKS_OVERSEER);
        lenient().when(schedulingProperties.getEmailCC()).thenReturn(EMAIL_CC);
    }

    @Test
    void sendEmail_WithThreeParams_ShouldSendEmail() {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        gmailSmtpService.sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_BODY);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mockMimeMessage);
    }

    @Test
    void sendEmail_WithFourParams_ShouldSendEmail() {
        doNothing().when(mailSender).send(any(MimeMessage.class));
        String[] customCC = new String[]{"custom@example.com"};

        gmailSmtpService.sendEmail(TEST_EMAIL, TEST_SUBJECT, customCC, TEST_BODY);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mockMimeMessage);
    }

    @Test
    void sendEmail_WithNullCC_ShouldUseDefaultCC() {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        gmailSmtpService.sendEmail(TEST_EMAIL, TEST_SUBJECT, null, TEST_BODY);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mockMimeMessage);
    }

    @Test
    void sendEmail_WithBlankRecipient_ShouldThrowEmailRecipientNotInformedException() {
        Exception exception = assertThrows(EmailRecipientNotInformedException.class, () -> gmailSmtpService.sendEmail("", TEST_SUBJECT, TEST_BODY));

        assertEquals(ERROR_EMAIL_RECIPIENT_NOT_INFORMED, exception.getMessage());
    }

    @Test
    void sendEmail_WithNullRecipient_ShouldThrowEmailRecipientNotInformedException() {
        Exception exception = assertThrows(EmailRecipientNotInformedException.class, () -> gmailSmtpService.sendEmail(null, TEST_SUBJECT, TEST_BODY));

        assertEquals(ERROR_EMAIL_RECIPIENT_NOT_INFORMED, exception.getMessage());
    }

    @Test
    void sendEmail_WhenMessagingExceptionOccurs_ShouldThrowRuntimeException() throws Exception {
        doThrow(new MessagingException("SMTP error")).when(mockMimeMessage).setSubject(anyString(), anyString());

        Exception exception = assertThrows(RuntimeException.class, () ->
            gmailSmtpService.sendEmail(TEST_EMAIL, TEST_SUBJECT, TEST_BODY)
        );

        assertTrue(exception.getMessage().contains("SMTP error"));
    }
}
