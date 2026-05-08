package org.crontalks.service;

import jakarta.mail.MessagingException;
import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.constants.WhatsAppProperties;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.entity.WhatsAppTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.crontalks.constants.Messages.EMAIL_TEST_SUBJECT;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL_TO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestServiceTest {

    @Mock private GmailSmtpService emailService;

    @Mock private SpeakerService speakerService;

    @Mock private EmailTemplate emailTemplate;

    @Mock private SchedulingProperties schedulingProperties;

    @Mock private WhatsAppService whatsAppService;

    @Mock private WhatsAppTemplate whatsAppTemplate;

    @Mock private WhatsAppProperties whatsAppProperties;

    @InjectMocks
    private TestService testService;

    private ScheduledTalk mockScheduledTalk;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String OVERSEER_EMAIL = "overseer@example.com";
    private static final String TEST_PHONE = "+1234567890";
    private static final String WHATSAPP_RESPONSE = "WhatsApp message sent";
    private static final String MOCKED_EMAIL_BODY = "Mocked email body";
    private static final String MOCKED_WHATSAPP_BODY = "Mocked WhatsApp body";

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
    void getMailTest_ShouldReturnEmailBody() {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(MOCKED_EMAIL_BODY);

        String response = testService.getMailTest();
        assertEquals(MOCKED_EMAIL_BODY, response);
    }

    @Test
    void sendMailTest_ShouldUseDefaultValues_WhenParametersAreNull() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(MOCKED_EMAIL_BODY);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);

        doNothing().when(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_TEST_SUBJECT), isNull(), eq(MOCKED_EMAIL_BODY));

        String response = testService.sendMailTest(null, null, null);
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY, OVERSEER_EMAIL, MOCKED_EMAIL_BODY), response);
    }

    @Test
    void sendMailTest_ShouldUseProvidedValues_WhenParametersAreProvided() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(MOCKED_EMAIL_BODY);
        String customSubject = "Custom Subject";
        String[] customCC = new String[]{"cc@example.com"};

        doNothing().when(emailService).sendEmail(eq(TEST_EMAIL), eq(customSubject), eq(customCC), eq(MOCKED_EMAIL_BODY));

        String response = testService.sendMailTest(TEST_EMAIL, customSubject, customCC);
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY, TEST_EMAIL, MOCKED_EMAIL_BODY), response);
    }

    @Test
    void sendMailTest_ShouldThrowException_WhenExceptionOccurs() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(emailTemplate.emailSpeakerTemplate(mockScheduledTalk)).thenReturn(MOCKED_EMAIL_BODY);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);

        doThrow(new MessagingException("Test exception")).when(emailService)
            .sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_TEST_SUBJECT), isNull(), eq(MOCKED_EMAIL_BODY));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.sendMailTest(null, null, null));
        assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), exception.getMessage());
    }

    @Test
    void sendWrongMailTest_ShouldSendEmailToOverseer() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(schedulingProperties.getReminderSpeakerNotInformedTemplate()).thenReturn("Template %s");

        doNothing().when(emailService).sendEmail(
            eq(OVERSEER_EMAIL),
            eq(String.format(ERROR_SENDING_EMAIL_TO, mockScheduledTalk.name())),
            isNull(),
            eq(String.format("Template %s", mockScheduledTalk.name()))
        );

        String response = testService.sendWrongMailTest();
        assertEquals(String.format(Messages.EMAIL_NOT_INFORMED_SUBJECT, mockScheduledTalk.name()), response);
    }

    @Test
    void sendWrongMailTest_ShouldThrowException_WhenExceptionOccurs() throws Exception {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(schedulingProperties.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(schedulingProperties.getReminderSpeakerNotInformedTemplate()).thenReturn("Template %s");

        doThrow(new MessagingException("Test exception")).when(emailService).sendEmail(
            anyString(), anyString(), isNull(), anyString()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.sendWrongMailTest());
        assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), exception.getMessage());
    }

    @Test
    void getWhatsAppTest_ShouldReturnWhatsAppBody() {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        when(whatsAppTemplate.whatsAppSpeakerTemplate(mockScheduledTalk)).thenReturn(MOCKED_WHATSAPP_BODY);

        String response = testService.getWhatsAppTest();
        assertEquals(MOCKED_WHATSAPP_BODY, response);
    }

    @Test
    void sendWhatsAppTest_ShouldReturnSuccessResponse_WhenWhatsAppIsSent() {
        when(whatsAppProperties.getWhatsAppTestPhoneNumber()).thenReturn(TEST_PHONE);
        when(whatsAppService.sendWhatsAppTest(eq(TEST_PHONE))).thenReturn(WHATSAPP_RESPONSE);

        String response = testService.sendWhatsAppTest();
        assertEquals(WHATSAPP_RESPONSE, response);
    }

    @Test
    void sendWhatsAppTest_ShouldThrowException_WhenHttpClientErrorExceptionOccurs() {
        when(whatsAppProperties.getWhatsAppTestPhoneNumber()).thenReturn(TEST_PHONE);
        when(whatsAppService.sendWhatsAppTest(eq(TEST_PHONE)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> testService.sendWhatsAppTest());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
