package org.crontalks.service;

import jakarta.mail.MessagingException;
import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.crontalks.constants.Messages.EMAIL_DEFAULT_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.constants.Messages.ERROR_GETTING_DATA_FROM_GSHEET;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL;
import static org.crontalks.constants.Messages.WARNING_SENDING_EMAIL_EMPTY_DATA;
import static org.crontalks.constants.Messages.WARNING_SENDING_EMAIL_SOME_EMPTY_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GmailServiceTest {

    @Mock
    private GmailSmtpService emailService;

    @Mock
    private SpeakerService speakerService;

    @Mock
    private EmailTemplate emailTemplate;

    @Mock
    private SchedulingProperties schedulingProperties;

    @InjectMocks
    private GmailService gmailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_BODY = "Test Body";
    private static final String OVERSEER_EMAIL = "overseer@example.com";

    private ScheduledTalk mockScheduledTalk;

    @BeforeEach
    void setUp() {
        mockScheduledTalk = ScheduledTalk.builder()
                .name("Test Speaker")
                .email("speaker@example.com")
                .congregation("Test Congregation")
                .outlineNumber(123)
                .outlineTitle("Test Outline")
                .outlineHasImages(true)
                .localDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void sendMail_ShouldReturnSuccessResponse_WhenEmailSentSuccessfully() throws Exception {
        doNothing().when(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));

        String response = gmailService.sendMail(TEST_EMAIL, TEST_SUBJECT, TEST_BODY);

        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY, TEST_EMAIL, TEST_BODY), response);
        verify(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendMail_ShouldThrowException_WhenExceptionOccurs() throws Exception {
        doThrow(new MessagingException("Test exception")).when(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMail(TEST_EMAIL, TEST_SUBJECT, TEST_BODY));
        assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), exception.getMessage());
        verify(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendMailCurrent_ShouldThrowException_WhenScheduledTalkIsNull() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailCurrent());
        assertEquals(ERROR_GETTING_DATA_FROM_GSHEET, exception.getMessage());
        verify(speakerService).getCurrentScheduledTalk();
    }

    @Test
    void sendMailCurrent_ShouldReturnSuccessResponse_WhenEmailSentSuccessfully() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        String expectedBody = "Mocked email body";
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
        
        doNothing().when(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));

        String response = gmailService.sendMailCurrent();

        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY, mockScheduledTalk.email(), expectedBody), response);
        verify(speakerService).getCurrentScheduledTalk();
        verify(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
    }

    @Test
    void sendMailCurrent_ShouldSendEmailToOverseer_WhenEmailRecipientNotInformedException() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);

        String expectedBody = "Mocked email body";
        String expectedOverseerBody = "Mocked overseer email body";
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
        when(emailTemplate.emailSpeakerNotInformedTemplate(mockScheduledTalk)).thenReturn(expectedOverseerBody);
        
        doThrow(new EmailRecipientNotInformedException("Email not informed"))
            .when(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
        
        doNothing().when(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_NOT_INFORMED_SUBJECT), eq(expectedOverseerBody));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailCurrent());
        
        assertEquals(String.format(ERROR_SENDING_EMAIL, "Email not informed"), exception.getMessage());
        verify(speakerService).getCurrentScheduledTalk();
        verify(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
        verify(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_NOT_INFORMED_SUBJECT), eq(expectedOverseerBody));
    }

    @Test
    void sendMailCurrent_ShouldThrowException_WhenGeneralExceptionOccurs() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

        String expectedBody = "Mocked email body";
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
        
        doThrow(new RuntimeException("Test exception"))
            .when(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailCurrent());

        assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), exception.getMessage());
        verify(speakerService).getCurrentScheduledTalk();
        verify(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
    }

    @Test
    void sendMailCurrentFails_ShouldSendEmailEmptyData_WhenScheduledTalkIsNull() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(emailTemplate.emailEmptyData()).thenReturn("Empty Data Template");

        gmailService.sendMailCurrentFails();

        verify(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(WARNING_SENDING_EMAIL_EMPTY_DATA), eq("Empty Data Template"));
    }

    @Test
    void sendMailCurrentFails_ShouldSendEmailSomeEmptyData_WhenScheduledTalkIsNotNull() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(emailTemplate.emailSomeEmptyData()).thenReturn("Some Empty Data Template");

        gmailService.sendMailCurrentFails();

        verify(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(WARNING_SENDING_EMAIL_SOME_EMPTY_DATA), eq("Some Empty Data Template"));
    }

    @Test
    void sendMailCurrentFails_ShouldHandleException() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(emailTemplate.emailEmptyData()).thenReturn("Empty Data Template");

        doThrow(new MessagingException("Exception during warning email"))
            .when(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(WARNING_SENDING_EMAIL_EMPTY_DATA), eq("Empty Data Template"));

        gmailService.sendMailCurrentFails();

        verify(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(WARNING_SENDING_EMAIL_EMPTY_DATA), eq("Empty Data Template"));
    }
}
