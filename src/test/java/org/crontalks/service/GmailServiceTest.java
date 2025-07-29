package org.crontalks.service;

import jakarta.mail.MessagingException;
import org.crontalks.constants.Params;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.exception.EmailRecipientNotInformedException;
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

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static org.crontalks.constants.Messages.EMAIL_DEFAULT_SUBJECT;
import static org.crontalks.constants.Messages.EMAIL_NOT_INFORMED_SUBJECT;
import static org.crontalks.constants.Messages.ERROR_GETTING_DATA_FROM_GSHEET;
import static org.crontalks.constants.Messages.ERROR_SENDING_EMAIL;
import static org.crontalks.entity.EmailTemplate.emailSpeakerNotInformedTemplate;
import static org.crontalks.entity.EmailTemplate.emailSpeakerTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GmailServiceTest {

    @Mock
    private GmailSmtpService emailService;

    @Mock
    private SpeakerService speakerService;

    @InjectMocks
    private GmailService gmailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_BODY = "Test Body";
    private static final String OVERSEER_EMAIL = "overseer@example.com";
    private static final String TALKS_OVERSEER = "Test Overseer";
    private static final String CONGREGATION_ADDRESS = "123 Test Street";
    private static final String CONGREGATION_GMAPS = "https://maps.google.com/test";
    private static final String VIDEO_DEPT_EMAIL = "video@example.com";

    private ScheduledTalk mockScheduledTalk;

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
    void sendMail_ShouldReturnSuccessResponse_WhenEmailSentSuccessfully() throws MessagingException, UnsupportedEncodingException {
        doNothing().when(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));

        // Mock the getSchedulingParam() static method
        var schedulingParam = mock(Params.Scheduling.class);
        when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(schedulingParam.getEmailCC()).thenReturn(new String[]{"cc@example.com"});

        ResponseEntity<?> response = gmailService.sendMail(TEST_EMAIL, TEST_SUBJECT, TEST_BODY);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendMail_ShouldReturnErrorResponse_WhenExceptionOccurs() throws MessagingException, UnsupportedEncodingException {
        doThrow(new MessagingException("Test exception")).when(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));

        // Mock the getSchedulingParam() static method
        var schedulingParam = mock(Params.Scheduling.class);
        when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(schedulingParam.getEmailCC()).thenReturn(new String[]{"cc@example.com"});

        ResponseEntity<?> response = gmailService.sendMail(TEST_EMAIL, TEST_SUBJECT, TEST_BODY);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), response.getBody());
        verify(emailService).sendEmail(eq(TEST_EMAIL), eq(TEST_SUBJECT), eq(TEST_BODY));
    }

    @Test
    void sendMailCurrent_ShouldReturnErrorResponse_WhenScheduledTalkIsNull() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);

        // Mock the getSchedulingParam() static method
        var schedulingParam = mock(Params.Scheduling.class);
        when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
        when(schedulingParam.getEmailCC()).thenReturn(new String[]{"cc@example.com"});

        ResponseEntity<?> response = gmailService.sendMailCurrent();

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ERROR_GETTING_DATA_FROM_GSHEET, response.getBody());
        verify(speakerService).getCurrentScheduledTalk();
    }

    @Test
    void sendMailCurrent_ShouldReturnSuccessResponse_WhenEmailSentSuccessfully() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        try (MockedStatic<org.crontalks.entity.EmailTemplate> mockedEmailTemplate = mockStatic(org.crontalks.entity.EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getEmailCC()).thenReturn(new String[]{"cc@example.com"});
            when(schedulingParam.getTalksOverseer()).thenReturn(TALKS_OVERSEER);
            when(schedulingParam.getCongregationAddress()).thenReturn(CONGREGATION_ADDRESS);
            when(schedulingParam.getCongregationGMaps()).thenReturn(CONGREGATION_GMAPS);
            when(schedulingParam.getVideoDeptEmail()).thenReturn(VIDEO_DEPT_EMAIL);

            // Mock the static methods from Params.Scheduling

            // Mock the emailSpeakerTemplate method
            String expectedBody = "Mocked email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            doNothing().when(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));

            ResponseEntity<?> response = gmailService.sendMailCurrent();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(speakerService).getCurrentScheduledTalk();
            verify(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
        }
    }

    @Test
    void sendMailCurrent_ShouldSendEmailToOverseer_WhenEmailRecipientNotInformedException() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        try (MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class);
             MockedStatic<org.crontalks.entity.EmailTemplate> mockedEmailTemplate = mockStatic(org.crontalks.entity.EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getEmailCC()).thenReturn(new String[]{"cc@example.com"});
            when(schedulingParam.getTalksOverseer()).thenReturn(TALKS_OVERSEER);
            when(schedulingParam.getCongregationAddress()).thenReturn(CONGREGATION_ADDRESS);
            when(schedulingParam.getCongregationGMaps()).thenReturn(CONGREGATION_GMAPS);
            when(schedulingParam.getVideoDeptEmail()).thenReturn(VIDEO_DEPT_EMAIL);
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(schedulingParam);

            // Mock the emailSpeakerTemplate and emailSpeakerNotInformedTemplate methods
            String expectedBody = "Mocked email body";
            String expectedOverseerBody = "Mocked overseer email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            mockedEmailTemplate.when(() -> emailSpeakerNotInformedTemplate(mockScheduledTalk)).thenReturn(expectedOverseerBody);
            
            doThrow(new EmailRecipientNotInformedException("Email not informed"))
                .when(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
            
            doNothing().when(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_NOT_INFORMED_SUBJECT), eq(expectedOverseerBody));

            ResponseEntity<?> response = gmailService.sendMailCurrent();

            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(String.format(ERROR_SENDING_EMAIL, "Email not informed"), response.getBody());
            verify(speakerService).getCurrentScheduledTalk();
            verify(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
            verify(emailService).sendEmail(eq(OVERSEER_EMAIL), eq(EMAIL_NOT_INFORMED_SUBJECT), eq(expectedOverseerBody));
        }
    }

    @Test
    void sendMailCurrent_ShouldReturnErrorResponse_WhenGeneralExceptionOccurs() throws MessagingException, UnsupportedEncodingException {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);
        
        try (MockedStatic<org.crontalks.entity.EmailTemplate> mockedEmailTemplate = mockStatic(org.crontalks.entity.EmailTemplate.class)) {
            
            // Mock the getSchedulingParam() static method
            var schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getOverseerEmail()).thenReturn(OVERSEER_EMAIL);
            when(schedulingParam.getEmailCC()).thenReturn(new String[]{"cc@example.com"});
            when(schedulingParam.getTalksOverseer()).thenReturn(TALKS_OVERSEER);
            when(schedulingParam.getCongregationAddress()).thenReturn(CONGREGATION_ADDRESS);
            when(schedulingParam.getCongregationGMaps()).thenReturn(CONGREGATION_GMAPS);
            when(schedulingParam.getVideoDeptEmail()).thenReturn(VIDEO_DEPT_EMAIL);

            // Mock the emailSpeakerTemplate method
            String expectedBody = "Mocked email body";
            mockedEmailTemplate.when(() -> emailSpeakerTemplate(mockScheduledTalk)).thenReturn(expectedBody);
            
            doThrow(new RuntimeException("Test exception"))
                .when(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));

            ResponseEntity<?> response = gmailService.sendMailCurrent();

            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(String.format(ERROR_SENDING_EMAIL, "Test exception"), response.getBody());
            verify(speakerService).getCurrentScheduledTalk();
            verify(emailService).sendEmail(eq(mockScheduledTalk.email()), eq(EMAIL_DEFAULT_SUBJECT), eq(expectedBody));
        }
    }
}
