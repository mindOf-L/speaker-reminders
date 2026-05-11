package org.crontalks.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "schedule")
public class SchedulingProperties {

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.from}")
    private String overseerEmail;

    @Value("${email.cc}")
    private String[] emailCC;

    @Value("${video-dept.email}")
    private String videoDeptEmail;

    @Value("${video-dept.overseer-name}")
    private String videoDeptOverseerName;

    @Value("${video-dept.overseer-phone}")
    private String videoDeptOverseerPhone;

    @Value("${MEETING-TIME:12:30}")
    private String meetingTime;

    @Value("${TALK-OVERSEER}")
    private String talksOverseer;

    @Value("${CONGREGATION-ADDRESS}")
    private String congregationAddress;

    @Value("${CONGREGATION-GMAPS}")
    private String congregationGMaps;

}
