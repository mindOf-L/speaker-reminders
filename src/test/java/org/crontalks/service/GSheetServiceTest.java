package org.crontalks.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GSheetServiceTest {

    @Mock
    private GSheetServiceAuthorize gSheetServiceAuthorize;

    @Mock
    private Sheets sheets;

    @Mock
    private Sheets.Spreadsheets spreadsheets;

    @Mock
    private Sheets.Spreadsheets.Values values;

    @Mock
    private Sheets.Spreadsheets.Values.Get get;

    @Mock
    private ValueRange valueRange;

    private GSheetService gSheetService;

    private static final String TEST_SHEET = "TestSheet";
    private static final String TEST_RANGE = "A1:D10";
    private static final String TEST_GSHEET_BOOK = "TestGSheetBook";

    @BeforeEach
    void setUp() throws IOException {
        // Mock the Sheets API chain
        when(gSheetServiceAuthorize.authorize()).thenReturn(sheets);
        when(sheets.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
        when(values.get(anyString(), anyString())).thenReturn(get);
        when(get.execute()).thenReturn(valueRange);

        // Create a new instance with our mocks
        org.crontalks.constants.Params.GSheets gSheets = mock(org.crontalks.constants.Params.GSheets.class);
        when(gSheets.getSheet()).thenReturn(TEST_GSHEET_BOOK);
        
        // Manually inject the mocks since we need to call the constructor
        gSheetService = new GSheetService(gSheetServiceAuthorize, gSheets);
    }

    @Test
    void getSheetValues_WithNoFilter_ShouldReturnAllValues() throws IOException {
        // Arrange
        List<List<Object>> testData = Arrays.asList(
            Arrays.asList("A1", "B1", "C1"),
            Arrays.asList("A2", "B2", "C2"),
            Arrays.asList("A3", "", "C3")
        );
        when(valueRange.getValues()).thenReturn(testData);

        // Act
        List<List<Object>> result = gSheetService.getSheetValues(TEST_SHEET, TEST_RANGE, false);

        // Assert
        assertEquals(3, result.size());
        assertEquals(testData, result);
        verify(values).get(eq(TEST_GSHEET_BOOK), eq(String.format("%s!%s", TEST_SHEET, TEST_RANGE)));
    }

    @Test
    void getSheetValues_WithFilter_ShouldFilterEmptyValues() throws IOException {
        // Arrange
        List<List<Object>> testData = Arrays.asList(
            Arrays.asList("A1", "B1", "C1"),
            Arrays.asList("", "B2", "C2"),
            Arrays.asList("A3", "", "C3")
        );
        when(valueRange.getValues()).thenReturn(testData);

        // Act
        List<List<Object>> result = gSheetService.getSheetValues(TEST_SHEET, TEST_RANGE, true, 0);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testData.get(0)));
        assertTrue(result.contains(testData.get(2)));
        verify(values).get(eq(TEST_GSHEET_BOOK), eq(String.format("%s!%s", TEST_SHEET, TEST_RANGE)));
    }

    @Test
    void getSheetValues_WithFilterDifferentColumn_ShouldFilterEmptyValuesInSpecifiedColumn() throws IOException {
        // Arrange
        List<List<Object>> testData = Arrays.asList(
            Arrays.asList("A1", "B1", "C1"),
            Arrays.asList("A2", "", "C2"),
            Arrays.asList("A3", "B3", "C3")
        );
        when(valueRange.getValues()).thenReturn(testData);

        // Act
        List<List<Object>> result = gSheetService.getSheetValues(TEST_SHEET, TEST_RANGE, true, 1);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testData.get(0)));
        assertTrue(result.contains(testData.get(2)));
        verify(values).get(eq(TEST_GSHEET_BOOK), eq(String.format("%s!%s", TEST_SHEET, TEST_RANGE)));
    }

    @Test
    void getSheetValues_WithNullResponse_ShouldReturnEmptyList() throws IOException {
        // Arrange
        when(valueRange.getValues()).thenReturn(null);

        // Act
        List<List<Object>> result = gSheetService.getSheetValues(TEST_SHEET, TEST_RANGE, false);

        // Assert
        assertTrue(result.isEmpty());
        verify(values).get(eq(TEST_GSHEET_BOOK), eq(String.format("%s!%s", TEST_SHEET, TEST_RANGE)));
    }

    @Test
    void getSheetValues_WithIOException_ShouldReturnEmptyList() throws IOException {
        // Arrange
        when(get.execute()).thenThrow(new IOException("Test exception"));

        // Act
        List<List<Object>> result = gSheetService.getSheetValues(TEST_SHEET, TEST_RANGE, false);

        // Assert
        assertTrue(result.isEmpty());
        verify(values).get(eq(TEST_GSHEET_BOOK), eq(String.format("%s!%s", TEST_SHEET, TEST_RANGE)));
    }

    @Test
    void getSheetValues_WithEmptyList_ShouldReturnEmptyList() throws IOException {
        // Arrange
        when(valueRange.getValues()).thenReturn(Collections.emptyList());

        // Act
        List<List<Object>> result = gSheetService.getSheetValues(TEST_SHEET, TEST_RANGE, false);

        // Assert
        assertTrue(result.isEmpty());
        verify(values).get(eq(TEST_GSHEET_BOOK), eq(String.format("%s!%s", TEST_SHEET, TEST_RANGE)));
    }
}
