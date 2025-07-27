package org.crontalks.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cron")
public class CronProperties {

    @Setter
    private String schedule;

    public String getSchedule() {
        return CronExpression.isValidExpression(schedule) ? schedule : "0 0 10 ? * MON";
    }

}
