package org.crontalks.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google")
public record GSheetsProperties (
    String sheet,
    String speakerSheet
) {}
