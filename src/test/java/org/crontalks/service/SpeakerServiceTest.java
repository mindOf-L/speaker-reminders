package org.crontalks.service;

import org.crontalks.constants.GSheetsProperties;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.mapper.ScheduledTalkMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpeakerServiceTest {

    @Mock
    private GSheetService gSheetService;

    @Mock
    private GSheetsProperties gSheetsProperties;

    @Mock
    private ScheduledTalkMapper scheduledTalkMapper;

    @InjectMocks
    private SpeakerService speakerService;

    private static final String TEST_SHEET_NAME = "TestSheet";

    @Test
    void getCurrentScheduledTalk_ShouldReturnScheduledTalk_WhenDataExists() {
        List<Object> rowData = Arrays.asList(
            "28/07/2025", // date
            "John Doe",   // name
            "Test Congregation", // congregation
            "123", // outline number
            "Test Outline", // outline title
            "+1234567890", // phone number
            "john@example.com", // email
            "true", // has images
            "false" // has video
        );
        List<List<Object>> sheetData = Collections.singletonList(rowData);

        when(gSheetsProperties.speakerSheet()).thenReturn(TEST_SHEET_NAME);
        when(gSheetService.getSheetValues(eq(TEST_SHEET_NAME), anyString(), eq(true))).thenReturn(sheetData);
        
        ScheduledTalk expectedTalk = ScheduledTalk.builder()
            .name("John Doe")
            .email("john@example.com")
            .congregation("Test Congregation")
            .outlineNumber(123)
            .outlineTitle("Test Outline")
            .outlineHasImages(true)
            .build();
            
        when(scheduledTalkMapper.toScheduledTalk(any())).thenReturn(expectedTalk);

        ScheduledTalk result = speakerService.getCurrentScheduledTalk();

        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());
        assertEquals("Test Congregation", result.congregation());
        assertEquals(123, result.outlineNumber());
        assertEquals("Test Outline", result.outlineTitle());
        assertTrue(result.outlineHasImages());
    }

    @Test
    void getCurrentScheduledTalk_ShouldReturnNull_WhenNoDataExists() {
        List<List<Object>> emptyData = Collections.emptyList();

        when(gSheetsProperties.speakerSheet()).thenReturn(TEST_SHEET_NAME);
        when(gSheetService.getSheetValues(eq(TEST_SHEET_NAME), anyString(), eq(true))).thenReturn(emptyData);

        ScheduledTalk result = speakerService.getCurrentScheduledTalk();

        assertNull(result);
    }
}
