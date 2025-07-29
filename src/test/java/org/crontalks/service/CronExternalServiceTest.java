package org.crontalks.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.crontalks.constants.Messages.EMAIL_TEST_SUBJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CronExternalServiceTest {

    @Mock
    private TestService testService;

    // Test-specific subclass that allows controlling the random behavior
    private static class TestableExternalService extends CronExternalService {
        private final boolean randomResult;

        public TestableExternalService(TestService testService, boolean randomResult) {
            super(testService);
            this.randomResult = randomResult;
        }

        // Override the random behavior for testing
        @Override
        protected boolean getRandomBoolean() {
            return randomResult;
        }
    }

    @Test
    void callExternalSystem_ShouldSucceed_WhenRandomReturnsTrue() throws Exception {
        // Create a testable service that will return true for getRandomBoolean()
        TestableExternalService testableService = new TestableExternalService(testService, true);
        
        when(testService.sendMailTest(isNull(), eq(EMAIL_TEST_SUBJECT), isNull()))
            .thenReturn(ResponseEntity.ok().build());
        
        testableService.callExternalSystem();
        
        verify(testService).sendMailTest(isNull(), eq(EMAIL_TEST_SUBJECT), isNull());
    }

    @Test
    void callExternalSystem_ShouldThrowException_WhenRandomReturnsFalse() {
        // Create a testable service that will return false for getRandomBoolean()
        TestableExternalService testableService = new TestableExternalService(testService, false);
        
        when(testService.sendWrongMailTest())
            .thenReturn(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build());
        
        Exception exception = assertThrows(Exception.class, testableService::callExternalSystem);
        
        assertEquals("Fail calling external service", exception.getMessage());
        verify(testService).sendWrongMailTest();
    }
}
