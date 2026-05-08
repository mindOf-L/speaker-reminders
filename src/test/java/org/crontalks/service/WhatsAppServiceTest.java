package org.crontalks.service;

import org.crontalks.constants.SchedulingProperties;
import org.crontalks.constants.WhatsAppProperties;
import org.crontalks.entity.ScheduledTalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WhatsAppServiceTest {

    @Mock
    private SpeakerService speakerService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WhatsAppProperties whatsAppProperties;

    @Mock
    private SchedulingProperties schedulingProperties;

    @InjectMocks
    private WhatsAppService whatsAppService;

    private ScheduledTalk mockScheduledTalk;
    private static final String TEST_PHONE = "+1234567890";
    private static final String TEST_WHATSAPP_URL = "https://graph.facebook.com/v17.0/%s/messages";
    private static final String TEST_WHATSAPP_PHONE_ID = "123456789";
    private static final String TEST_TEMPLATE_NAME_FIRST = "first_template";
    private static final String TEST_TEMPLATE_NAME_SECOND = "second_template";

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
    void sendWhatsAppTest_ShouldThrowUnauthorizedException_WhenCallingWhatsAppAPI() {
        when(whatsAppProperties.getWhatsAppUrl()).thenReturn(TEST_WHATSAPP_URL);
        when(whatsAppProperties.getWhatsAppPhoneNumberId()).thenReturn(TEST_WHATSAPP_PHONE_ID);
        when(whatsAppProperties.getWhatsAppTemplateNameFirst()).thenReturn(TEST_TEMPLATE_NAME_FIRST);
        when(whatsAppProperties.getWhatsAppTemplateNameSecond()).thenReturn(TEST_TEMPLATE_NAME_SECOND);
        when(whatsAppProperties.getOutlineImagesTemplateWhatsApp()).thenReturn("Outline images template %s %s %s");
        when(whatsAppProperties.getSpeakerCustomImagesTemplateWhatsApp()).thenReturn("Custom images template %s %s %s");
        
        when(whatsAppProperties.createMap(anyString(), anyString())).thenAnswer(invocation -> {
            return Map.of(invocation.getArgument(0).toString(), invocation.getArgument(1).toString());
        });

        when(schedulingProperties.getTalksOverseer()).thenReturn("Test Overseer");
        when(schedulingProperties.getMeetingTime()).thenReturn("19:00");
        when(schedulingProperties.getCongregationAddress()).thenReturn("123 Test Street");
        when(schedulingProperties.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
        when(schedulingProperties.getVideoDeptEmail()).thenReturn("video@example.com");
        when(schedulingProperties.getVideoDeptOverseerName()).thenReturn("Video Overseer");
        when(schedulingProperties.getVideoDeptOverseerPhone()).thenReturn("+1234567890");

        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        HttpClientErrorException exception = assertThrows(
            HttpClientErrorException.class, 
            () -> whatsAppService.sendWhatsAppTest(TEST_PHONE)
        );
        
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void sendWhatsAppTest_ShouldThrowException_WhenRestTemplateThrowsException() {
        when(whatsAppProperties.getWhatsAppUrl()).thenReturn(TEST_WHATSAPP_URL);
        when(whatsAppProperties.getWhatsAppPhoneNumberId()).thenReturn(TEST_WHATSAPP_PHONE_ID);
        when(whatsAppProperties.getWhatsAppTemplateNameFirst()).thenReturn(TEST_TEMPLATE_NAME_FIRST);
        when(whatsAppProperties.getWhatsAppTemplateNameSecond()).thenReturn(TEST_TEMPLATE_NAME_SECOND);
        when(whatsAppProperties.getOutlineImagesTemplateWhatsApp()).thenReturn("Outline images template %s %s %s");
        when(whatsAppProperties.getSpeakerCustomImagesTemplateWhatsApp()).thenReturn("Custom images template %s %s %s");
        
        when(whatsAppProperties.createMap(anyString(), anyString())).thenAnswer(invocation -> {
            return Map.of(invocation.getArgument(0).toString(), invocation.getArgument(1).toString());
        });
        
        when(schedulingProperties.getTalksOverseer()).thenReturn("Test Overseer");
        when(schedulingProperties.getMeetingTime()).thenReturn("19:00");
        when(schedulingProperties.getCongregationAddress()).thenReturn("123 Test Street");
        when(schedulingProperties.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
        when(schedulingProperties.getVideoDeptEmail()).thenReturn("video@example.com");
        when(schedulingProperties.getVideoDeptOverseerName()).thenReturn("Video Overseer");
        when(schedulingProperties.getVideoDeptOverseerPhone()).thenReturn("+1234567890");

        when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        assertThrows(HttpClientErrorException.class, () -> whatsAppService.sendWhatsAppTest(TEST_PHONE));
    }
}
