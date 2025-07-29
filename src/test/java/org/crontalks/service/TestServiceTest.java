package org.crontalks.service;

import jakarta.mail.MessagingException;
import org.crontalks.constants.Params;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.entity.WhatsAppTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static org.crontalks.constants.Messages.EMAIL_TEST_SUBJECT;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL_TO;
import static org.crontalks.constants.Messages.ERROR_SENDING_WHATSAPP;
import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.constants.Params.WhatsApp.getWhatsAppParam;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;
import static org.crontalks.entity.WhatsAppTemplate.whatsAppSpeakerTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestServiceTest {

    @Mock
    private GmailSmtpService emailService;

    @Mock
    private SpeakerService speakerService;

    @Mock
    private WhatsAppService whatsAppService;

    @InjectMocks
    private TestService testService;

    private ScheduledTalk mockScheduledTalk;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String OVERSEER_EMAIL = "overseer@example.com";
    private static final String TEST_PHONE = "+1234567890";
    private static final String WHATSAPP_RESPONSE = "WhatsApp message sent";

    @BeforeEach
    void setUp() {
        // Create a mock ScheduledTalk for testing
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
        
        try (MockedStatic<EmailTemplate> mockedEmailTemplate = mockStatic(EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getTalksOverseer()).thenReturn("Test Overseer");
            when(schedulingParam.getCongregationAddress()).thenReturn("123 Test Street");
            when(schedulingParam.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");

            // Mock the emailSpeakerTemplate method
            String expectedBody = "Mocked email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            ResponseEntity<?> response = testService.getMailTest();
            
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedBody, response.getBody());
        }
    }

    @Test
    void sendMailTest_ShouldUseDefaultValues_WhenParametersAreNull() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        try (MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class);
             MockedStatic<EmailTemplate> mockedEmailTemplate = mockStatic(EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getTalksOverseer()).thenReturn("Test Overseer");
            when(schedulingParam.getCongregationAddress()).thenReturn("123 Test Street");
            when(schedulingParam.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(schedulingParam);

            // Mock the emailSpeakerTemplate method
            String expectedBody = "Mocked email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            doNothing().when(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_TEST_SUBJECT), isNull(), eq(expectedBody));

            ResponseEntity<?> response = testService.sendMailTest(null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void sendMailTest_ShouldUseProvidedValues_WhenParametersAreProvided() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        String customSubject = "Custom Subject";
        String[] customCC = new String[]{"cc@example.com"};
        
        try (MockedStatic<EmailTemplate> mockedEmailTemplate = mockStatic(EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getTalksOverseer()).thenReturn("Test Overseer");
            when(schedulingParam.getCongregationAddress()).thenReturn("123 Test Street");
            when(schedulingParam.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");

            // Mock the emailSpeakerTemplate method
            String expectedBody = "Mocked email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            doNothing().when(emailService).sendEmail(eq(TEST_EMAIL), eq(customSubject), eq(customCC), eq(expectedBody));

            ResponseEntity<?> response = testService.sendMailTest(TEST_EMAIL, customSubject, customCC);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void sendMailTest_ShouldReturnErrorResponse_WhenExceptionOccurs() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        try (MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class);
             MockedStatic<EmailTemplate> mockedEmailTemplate = mockStatic(EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getTalksOverseer()).thenReturn("Test Overseer");
            when(schedulingParam.getCongregationAddress()).thenReturn("123 Test Street");
            when(schedulingParam.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(schedulingParam);

            // Mock the emailSpeakerTemplate method
            String expectedBody = "Mocked email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            doThrow(new MessagingException("Test exception")).when(emailService)
                .sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_TEST_SUBJECT), isNull(), eq(expectedBody));

            ResponseEntity<?> response = testService.sendMailTest(null, null, null);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), response.getBody());
        }
    }

    @Test
    void sendWrongMailTest_ShouldSendEmailToOverseer() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

        try (MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class)) {
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(mock(Params.Scheduling.class));
            when(getSchedulingParam().getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(Params.Scheduling.getReminderSpeakerNotInformedTemplate()).thenReturn("Template %s");

            doNothing().when(emailService).sendEmail(
                eq(OVERSEER_EMAIL),
                eq(String.format(ERROR_SENDING_EMAIL_TO, mockScheduledTalk.name())),
                isNull(),
                eq(String.format("Template %s", mockScheduledTalk.name()))
            );

            ResponseEntity<?> response = testService.sendWrongMailTest();

            assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        }
    }

    @Test
    void sendWrongMailTest_ShouldReturnErrorResponse_WhenExceptionOccurs() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

        try (MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class)) {
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(mock(Params.Scheduling.class));
            when(getSchedulingParam().getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(Params.Scheduling.getReminderSpeakerNotInformedTemplate()).thenReturn("Template %s");
            
            doThrow(new MessagingException("Test exception")).when(emailService).sendEmail(
                anyString(), anyString(), isNull(), anyString()
            );

            ResponseEntity<?> response = testService.sendWrongMailTest();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), response.getBody());
        }
    }

    @Test
    void getWhatsAppTest_ShouldReturnWhatsAppBody() {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        try (MockedStatic<WhatsAppTemplate> mockedWhatsAppTemplate = mockStatic(WhatsAppTemplate.class)) {
            
            // Mock the getWhatsAppParam() static method
            var whatsAppParam = mock(Params.WhatsApp.class);
            when(whatsAppParam.getWhatsAppTestPhoneNumber()).thenReturn(TEST_PHONE);
            when(whatsAppParam.getReminderSpeakerTemplateWhatsAppV2()).thenReturn("WhatsApp template %s");
            when(whatsAppParam.getOutlineImagesTemplateWhatsApp()).thenReturn("Outline images %s");
            when(whatsAppParam.getSpeakerCustomImagesTemplateWhatsApp()).thenReturn("Custom images %s");

            // Mock the getSchedulingParam() static method for WhatsAppTemplate
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");
            when(schedulingParam.getVideoDeptOverseerName()).thenReturn("Video Overseer");
            when(schedulingParam.getVideoDeptOverseerPhone()).thenReturn("+1234567890");

            // Mock the whatsAppSpeakerTemplate method
            String expectedBody = "Mocked WhatsApp body";
            mockedWhatsAppTemplate.when(() -> whatsAppSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            ResponseEntity<?> response = testService.getWhatsAppTest();
            
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedBody, response.getBody());
        }
    }

    @Test
    void sendWhatsAppTest_ShouldReturnSuccessResponse_WhenWhatsAppIsSent() {
        try (MockedStatic<Params.WhatsApp> mockedWhatsApp = mockStatic(Params.WhatsApp.class)) {
            mockedWhatsApp.when(Params.WhatsApp::getWhatsAppParam).thenReturn(mock(Params.WhatsApp.class));
            when(getWhatsAppParam().getWhatsAppTestPhoneNumber()).thenReturn(TEST_PHONE);
            
            when(whatsAppService.sendWhatsAppTest(eq(TEST_PHONE))).thenReturn(WHATSAPP_RESPONSE);

            ResponseEntity<?> response = testService.sendWhatsAppTest();

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(WHATSAPP_RESPONSE, response.getBody());
        }
    }

    @Test
    void sendWhatsAppTest_ShouldReturnErrorResponse_WhenHttpClientErrorExceptionOccurs() {
        try (MockedStatic<Params.WhatsApp> mockedWhatsApp = mockStatic(Params.WhatsApp.class)) {
            mockedWhatsApp.when(Params.WhatsApp::getWhatsAppParam).thenReturn(mock(Params.WhatsApp.class));
            when(getWhatsAppParam().getWhatsAppTestPhoneNumber()).thenReturn(TEST_PHONE);
            
            when(whatsAppService.sendWhatsAppTest(eq(TEST_PHONE)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

            ResponseEntity<?> response = testService.sendWhatsAppTest();

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(String.format(ERROR_SENDING_WHATSAPP, TEST_PHONE), response.getBody());
        }
    }
}
