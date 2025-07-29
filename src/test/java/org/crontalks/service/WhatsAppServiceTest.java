package org.crontalks.service;

import com.google.common.collect.ImmutableMap;
import org.crontalks.constants.Params;
import org.crontalks.entity.ScheduledTalk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.crontalks.constants.Params.WhatsApp.createImmutableMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WhatsAppServiceTest {

    @Mock
    private SpeakerService speakerService;

    @Mock
    private RestTemplate restTemplate;

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
        try (MockedStatic<Params.WhatsApp> mockedWhatsApp = mockStatic(Params.WhatsApp.class);
             MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class)) {
            
            // Mock WhatsApp parameters
            Params.WhatsApp whatsAppParam = mock(Params.WhatsApp.class);
            when(whatsAppParam.getWhatsAppUrl()).thenReturn(TEST_WHATSAPP_URL);
            when(whatsAppParam.getWhatsAppPhoneNumberId()).thenReturn(TEST_WHATSAPP_PHONE_ID);
            when(whatsAppParam.getWhatsAppTemplateNameFirst()).thenReturn(TEST_TEMPLATE_NAME_FIRST);
            when(whatsAppParam.getWhatsAppTemplateNameSecond()).thenReturn(TEST_TEMPLATE_NAME_SECOND);
            when(whatsAppParam.getOutlineImagesTemplateWhatsApp()).thenReturn("Outline images template %s %s %s");
            when(whatsAppParam.getSpeakerCustomImagesTemplateWhatsApp()).thenReturn("Custom images template %s %s %s");
            mockedWhatsApp.when(Params.WhatsApp::getWhatsAppParam).thenReturn(whatsAppParam);

            // Mock Scheduling parameters
            Params.Scheduling schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getTalksOverseer()).thenReturn("Test Overseer");
            when(schedulingParam.getMeetingTime()).thenReturn("19:00");
            when(schedulingParam.getCongregationAddress()).thenReturn("123 Test Street");
            when(schedulingParam.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");
            when(schedulingParam.getVideoDeptOverseerName()).thenReturn("Video Overseer");
            when(schedulingParam.getVideoDeptOverseerPhone()).thenReturn("+1234567890");
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(schedulingParam);

            // Mock createImmutableMap
            mockedWhatsApp.when(() -> createImmutableMap(anyString(), anyString())).thenAnswer(invocation -> {
                String key = invocation.getArgument(0);
                String value = invocation.getArgument(1);
                return ImmutableMap.of(key, value);
            });
            
            // Mock SpeakerService
            when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

            // Expect HttpClientErrorException.Unauthorized to be thrown
            HttpClientErrorException.Unauthorized exception = assertThrows(
                HttpClientErrorException.Unauthorized.class, 
                () -> whatsAppService.sendWhatsAppTest(TEST_PHONE)
            );
            
            // Verify the exception details if needed
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        }
    }

    @Test
    void sendWhatsAppTest_ShouldThrowException_WhenRestTemplateThrowsException() {
        try (MockedStatic<Params.WhatsApp> mockedWhatsApp = mockStatic(Params.WhatsApp.class);
             MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class)) {
            
            // Mock WhatsApp parameters
            Params.WhatsApp whatsAppParam = mock(Params.WhatsApp.class);
            when(whatsAppParam.getWhatsAppUrl()).thenReturn(TEST_WHATSAPP_URL);
            when(whatsAppParam.getWhatsAppPhoneNumberId()).thenReturn(TEST_WHATSAPP_PHONE_ID);
            when(whatsAppParam.getWhatsAppTemplateNameFirst()).thenReturn(TEST_TEMPLATE_NAME_FIRST);
            when(whatsAppParam.getWhatsAppTemplateNameSecond()).thenReturn(TEST_TEMPLATE_NAME_SECOND);
            when(whatsAppParam.getOutlineImagesTemplateWhatsApp()).thenReturn("Outline images template %s %s %s");
            when(whatsAppParam.getSpeakerCustomImagesTemplateWhatsApp()).thenReturn("Custom images template %s %s %s");
            mockedWhatsApp.when(Params.WhatsApp::getWhatsAppParam).thenReturn(whatsAppParam);
            
            // Mock Scheduling parameters
            Params.Scheduling schedulingParam = mock(Params.Scheduling.class);
            when(schedulingParam.getTalksOverseer()).thenReturn("Test Overseer");
            when(schedulingParam.getMeetingTime()).thenReturn("19:00");
            when(schedulingParam.getCongregationAddress()).thenReturn("123 Test Street");
            when(schedulingParam.getCongregationGMaps()).thenReturn("https://maps.google.com/test");
            when(schedulingParam.getVideoDeptEmail()).thenReturn("video@example.com");
            when(schedulingParam.getVideoDeptOverseerName()).thenReturn("Video Overseer");
            when(schedulingParam.getVideoDeptOverseerPhone()).thenReturn("+1234567890");
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(schedulingParam);
            
            // Mock createImmutableMap
            mockedWhatsApp.when(() -> createImmutableMap(anyString(), anyString())).thenAnswer(invocation -> {
                String key = invocation.getArgument(0);
                String value = invocation.getArgument(1);
                return ImmutableMap.of(key, value);
            });
            
            // Mock RestTemplate to throw exception
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));
            when(speakerService.getCurrentScheduledTalk()).thenReturn(mockScheduledTalk);

            assertThrows(HttpClientErrorException.class, () -> whatsAppService.sendWhatsAppTest(TEST_PHONE));
        }
    }
}
