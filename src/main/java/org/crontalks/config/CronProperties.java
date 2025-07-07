package org.crontalks.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cron")
public class CronProperties {

    @Setter @Getter
    private String schedule;

}
