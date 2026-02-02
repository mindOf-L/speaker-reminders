package org.crontalks.entity;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScheduledTalk (
    LocalDateTime localDateTime,
    String name,
    String congregation,
    int outlineNumber,
    String outlineTitle,
    String phoneNumber,
    String email,
    boolean outlineHasImages,
    boolean outlineHasVideo
) { }
