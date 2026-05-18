package org.crontalks.service;

import org.crontalks.constants.Messages;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.entity.EmailTemplate;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.exception.EmailRecipientNotInformedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GmailServiceTest {

    @Mock
    private EmailTemplate emailTemplate;

    @Mock
    private SchedulingProperties schedulingProperties;

    @Mock
    private GmailSmtpService gmailSmtpService;

    @Mock
    private SpeakerService speakerService;

    @InjectMocks
    private GmailService gmailService;

    @Test
    void sendMailCurrent_WhenScheduledTalkIsNull_ShouldThrowRuntimeException() {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailCurrent());
        assertEquals(Messages.ERROR_GETTING_DATA_FROM_GSHEET, exception.getMessage());
    }

    @Test
    void sendMailCurrent_WhenSpecialOutlineNumber_ShouldSendSpecialOutlineEmail() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(950)
                .email("speaker@example.com")
                .build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpecialOutlineScheduledTemplate(talk)).thenReturn("Special Body");
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");

        String response = gmailService.sendMailCurrent();

        verify(gmailSmtpService).sendEmail("overseer@example.com", Messages.EMAIL_SPECIAL_OUTLINE_SUBJECT, "Special Body");
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, "overseer@example.com"), response);
    }

    @Test
    void sendMailCurrent_WhenNormalOutlineAndNoWhatsApp_ShouldSendOnlyEmail() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(100)
                .email("speaker@example.com")
                .hasWhatsApp(false)
                .localDateTime(LocalDateTime.now())
                .build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpeakerTemplate(talk)).thenReturn("Speaker Body");
        when(emailTemplate.processEmailSpeakerWhatsAppTemplate(talk)).thenReturn("WhatsApp Body");

        String response = gmailService.sendMailCurrent();

        verify(gmailSmtpService).sendEmail("speaker@example.com", Messages.EMAIL_DEFAULT_SUBJECT, "Speaker Body");
        verify(gmailSmtpService, never()).sendEmail(anyString(), anyString(), any(String[].class), anyString());
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, "speaker@example.com"), response);
    }

    @Test
    void sendMailCurrent_WhenNormalOutlineAndHasWhatsAppAndCurrentWeek_ShouldSendEmailAndWhatsApp() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(100)
                .email("speaker@example.com")
                .hasWhatsApp(true)
                .localDateTime(LocalDateTime.now())
                .build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpeakerTemplate(talk)).thenReturn("Speaker Body");
        when(emailTemplate.processEmailSpeakerWhatsAppTemplate(talk)).thenReturn("WhatsApp Body");
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");

        String response = gmailService.sendMailCurrent();

        verify(gmailSmtpService).sendEmail("speaker@example.com", Messages.EMAIL_DEFAULT_SUBJECT, "Speaker Body");
        verify(gmailSmtpService).sendEmail(
                eq("overseer@example.com"),
                eq(Messages.EMAIL_WHATSAPP_REMINDER_SUBJECT),
                eq(new String[]{"overseer@example.com"}),
                eq("WhatsApp Body")
        );
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, "speaker@example.com"), response);
    }

    @Test
    void sendMailCurrent_WhenNormalOutlineAndHasWhatsAppAndDifferentWeek_ShouldSendEmailAndWhatsApp() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(100)
                .email("speaker@example.com")
                .hasWhatsApp(true)
                .localDateTime(LocalDateTime.now().plusWeeks(2))
                .build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpeakerTemplate(talk)).thenReturn("Speaker Body");
        when(emailTemplate.processEmailSpeakerWhatsAppTemplate(talk)).thenReturn("WhatsApp Body");
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");

        String response = gmailService.sendMailCurrent();

        verify(gmailSmtpService).sendEmail("speaker@example.com", Messages.EMAIL_DEFAULT_SUBJECT, "Speaker Body");
        verify(gmailSmtpService).sendEmail(
                eq("overseer@example.com"),
                eq(Messages.EMAIL_WHATSAPP_REMINDER_SUBJECT),
                eq(new String[]{"overseer@example.com"}),
                eq("WhatsApp Body")
        );
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, "speaker@example.com"), response);
    }

    @Test
    void sendMailCurrent_WhenEmailRecipientNotInformed_ShouldSendWarningAndThrowRuntimeException() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(100)
                .email("speaker@example.com")
                .hasWhatsApp(false)
                .localDateTime(LocalDateTime.now())
                .build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpeakerTemplate(talk)).thenReturn("Speaker Body");
        
        doThrow(new EmailRecipientNotInformedException("Recipient not informed")).when(gmailSmtpService)
                .sendEmail(eq("speaker@example.com"), anyString(), anyString());
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");
        when(emailTemplate.emailSpeakerNotInformedTemplate(talk)).thenReturn("Not Informed Body");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailCurrent());
        
        verify(gmailSmtpService).sendEmail("overseer@example.com", Messages.EMAIL_NOT_INFORMED_SUBJECT, "Not Informed Body");
        assertTrue(exception.getMessage().contains(String.format(Messages.ERROR_SENDING_EMAIL, "Recipient not informed").substring(0, 10)));
    }

    @Test
    void sendMailCurrent_WhenGenericExceptionOccurs_ShouldThrowRuntimeException() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(100)
                .email("speaker@example.com")
                .hasWhatsApp(false)
                .localDateTime(LocalDateTime.now())
                .build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpeakerTemplate(talk)).thenReturn("Speaker Body");
        
        doThrow(new RuntimeException("SMTP Connection failed")).when(gmailSmtpService)
                .sendEmail(anyString(), anyString(), anyString());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailCurrent());
        assertTrue(exception.getMessage().contains("SMTP Connection failed"));
    }

    @Test
    void sendMailNext4Week_WhenScheduledTalkIsNull_ShouldThrowRuntimeException() {
        when(speakerService.getNext4WeekScheduledTalk()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> gmailService.sendMailNext4Week());
        assertEquals(Messages.ERROR_GETTING_DATA_FROM_GSHEET, exception.getMessage());
    }

    @Test
    void sendMailNext4Week_WhenSpecialOutlineNumber_ShouldSendSpecialOutlineEmail() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(901)
                .email("speaker@example.com")
                .build();
        when(speakerService.getNext4WeekScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpecialOutlineScheduledTemplate(talk)).thenReturn("Special Body");
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");

        String response = gmailService.sendMailNext4Week();

        verify(gmailSmtpService).sendEmail("overseer@example.com", Messages.EMAIL_SPECIAL_OUTLINE_SUBJECT, "Special Body");
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, "overseer@example.com"), response);
    }

    @Test
    void sendMailNext4Week_WhenNormalOutline_ShouldSendEmail() {
        ScheduledTalk talk = ScheduledTalk.builder()
                .outlineNumber(120)
                .email("speaker@example.com")
                .hasWhatsApp(false)
                .localDateTime(LocalDateTime.now())
                .build();
        when(speakerService.getNext4WeekScheduledTalk()).thenReturn(talk);
        when(emailTemplate.processEmailSpeakerNext4WeekTemplate(talk)).thenReturn("Speaker Body");
        when(emailTemplate.processEmailSpeakerNext4WeekWhatsAppTemplate(talk)).thenReturn("WhatsApp Body");

        String response = gmailService.sendMailNext4Week();

        verify(gmailSmtpService).sendEmail("speaker@example.com", Messages.EMAIL_DEFAULT_NEXT_4_WEEK_SUBJECT, "Speaker Body");
        assertEquals(String.format(Messages.EMAIL_SENT_CORRECTLY_NO_CONTENT, "speaker@example.com"), response);
    }

    @Test
    void sendMailCurrentFails_WhenTalkIsNull_ShouldSendEmptyDataWarning() {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");
        when(emailTemplate.emailEmptyData()).thenReturn("Empty Data Body");

        gmailService.sendMailCurrentFails();

        verify(gmailSmtpService).sendEmail("overseer@example.com", Messages.WARNING_SENDING_EMAIL_EMPTY_DATA, "Empty Data Body");
    }

    @Test
    void sendMailCurrentFails_WhenTalkIsNotNull_ShouldSendSomeEmptyDataWarning() {
        ScheduledTalk talk = ScheduledTalk.builder().build();
        when(speakerService.getCurrentScheduledTalk()).thenReturn(talk);
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");
        when(emailTemplate.emailSomeEmptyData()).thenReturn("Some Empty Data Body");

        gmailService.sendMailCurrentFails();

        verify(gmailSmtpService).sendEmail("overseer@example.com", Messages.WARNING_SENDING_EMAIL_SOME_EMPTY_DATA, "Some Empty Data Body");
    }

    @Test
    void sendMailCurrentFails_WhenExceptionOccurs_ShouldCatchExceptionAndNotThrow() {
        when(speakerService.getCurrentScheduledTalk()).thenReturn(null);
        when(schedulingProperties.getOverseerEmail()).thenReturn("overseer@example.com");
        when(emailTemplate.emailEmptyData()).thenReturn("Empty Data Body");
        
        doThrow(new RuntimeException("SMTP failed")).when(gmailSmtpService)
                .sendEmail(anyString(), anyString(), anyString());

        // Should not throw any exception
        gmailService.sendMailCurrentFails();
        
        verify(gmailSmtpService).sendEmail("overseer@example.com", Messages.WARNING_SENDING_EMAIL_EMPTY_DATA, "Empty Data Body");
    }
}
