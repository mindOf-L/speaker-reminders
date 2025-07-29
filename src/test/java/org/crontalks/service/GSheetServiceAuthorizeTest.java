package org.crontalks.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GSheetServiceAuthorizeTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private GSheetServiceAuthorize gSheetServiceAuthorize;

    private static final String TEST_JSON = "{\"type\":\"service_account\",\"project_id\":\"test-project\"}";
    private static final String TEST_CLASSPATH = "classpath:cred.json";
    private static final String TEST_FILEPATH = "/path/to/cred.json";

    @Test
    void getFromResource_WithClasspathResource_ShouldReturnInputStream() {
        ReflectionTestUtils.setField(gSheetServiceAuthorize, "credentialsResource", TEST_CLASSPATH);
        Resource mockResource = new ByteArrayResource(TEST_JSON.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource(eq(TEST_CLASSPATH))).thenReturn(mockResource);

        // We can't easily test the full authorize() method without extensive mocking of Google APIs
        // So we'll test the private getFromResource() method using reflection
        Object result = ReflectionTestUtils.invokeMethod(gSheetServiceAuthorize, "getFromResource");

        // Assert
        assertNotNull(result);
    }

    @Test
    void getFromResource_WithJsonString_ShouldReturnInputStream() {
        ReflectionTestUtils.setField(gSheetServiceAuthorize, "credentialsResource", TEST_JSON);

        Object result = ReflectionTestUtils.invokeMethod(gSheetServiceAuthorize, "getFromResource");

        // Assert
        assertNotNull(result);
    }

    @Test
    void getFromResource_WithInvalidPath_ShouldThrowException() {
        ReflectionTestUtils.setField(gSheetServiceAuthorize, "credentialsResource", TEST_FILEPATH);
        // No need to stub resourceLoader as it's not used in this test case
        // The invalid file path will cause a FileNotFoundException (subclass of IOException)

        Exception exception = assertThrows(Exception.class, () -> ReflectionTestUtils.invokeMethod(gSheetServiceAuthorize, "getFromResource"));
        
        // When using ReflectionTestUtils, exceptions are wrapped in UndeclaredThrowableException
        // So we need to check the cause of the exception
        Throwable cause = exception.getCause();
        assertInstanceOf(IOException.class, cause, "Expected IOException but got: " + cause.getClass().getName());
    }
}
