package org.crontalks.service;

import org.crontalks.constants.Params;
import org.crontalks.entity.ScheduledTalk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.crontalks.constants.Params.GSheets.getGSheetsParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpeakerServiceTest {

    @Mock
    private GSheetService gSheetService;

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

        try (
            MockedStatic<Params.GSheets> mockedGSheets = mockStatic(Params.GSheets.class);
            MockedStatic<Params.Scheduling> mockedScheduling = mockStatic(Params.Scheduling.class)
        ) {
            mockedGSheets.when(Params.GSheets::getGSheetsParam).thenReturn(mock(Params.GSheets.class));
            when(getGSheetsParam().getThisWeekSpeaker()).thenReturn(TEST_SHEET_NAME);
            when(gSheetService.getSheetValues(eq(TEST_SHEET_NAME), anyString(), eq(true))).thenReturn(sheetData);
            
            Params.Scheduling schedulingParam = mock(Params.Scheduling.class);
            mockedScheduling.when(Params.Scheduling::getSchedulingParam).thenReturn(schedulingParam);
            when(schedulingParam.getMeetingTime()).thenReturn("19:30");

            ScheduledTalk result = speakerService.getCurrentScheduledTalk();

            assertEquals("John Doe", result.name());
            assertEquals("john@example.com", result.email());
            assertEquals("Test Congregation", result.congregation());
            assertEquals(123, result.outlineNumber());
            assertEquals("Test Outline", result.outlineTitle());
            assertTrue(result.outlineHasImages());
        }
    }

    @Test
    void getCurrentScheduledTalk_ShouldReturnNull_WhenNoDataExists() {
        List<List<Object>> emptyData = Collections.emptyList();

        try (MockedStatic<Params.GSheets> mockedGSheets = mockStatic(Params.GSheets.class)) {
            mockedGSheets.when(Params.GSheets::getGSheetsParam).thenReturn(mock(Params.GSheets.class));
            when(getGSheetsParam().getThisWeekSpeaker()).thenReturn(TEST_SHEET_NAME);
            when(gSheetService.getSheetValues(eq(TEST_SHEET_NAME), anyString(), eq(true))).thenReturn(emptyData);
            
            Params.Scheduling schedulingParam = mock(Params.Scheduling.class);
            lenient().when(schedulingParam.getMeetingTime()).thenReturn("19:30");

            ScheduledTalk result = speakerService.getCurrentScheduledTalk();

            assertNull(result);
        }
    }
}
