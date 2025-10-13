package org.crontalks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(value = "classpath:application-test.yml", properties = "spring.profiles.include=test")
class ReminderApplicationTests {

    @Disabled
    @Test
    void contextLoads() { }

}
