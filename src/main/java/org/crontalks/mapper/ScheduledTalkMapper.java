package org.crontalks.mapper;

import org.crontalks.entity.ScheduledTalk;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.crontalks.constants.Params.Scheduling.getSchedulingParam;
import static org.crontalks.mapper.OptionalMapper.mapToInt;
import static org.crontalks.mapper.OptionalMapper.mapToString;

public class ScheduledTalkMapper {

    public static ScheduledTalk toScheduledTalk(List<Object> row) {
        var talkDate = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(row.getFirst().toString()));
        var talkTime = LocalTime.from(DateTimeFormatter.ofPattern("HH:mm").parse(getSchedulingParam().getMeetingTime()));

        return ScheduledTalk.builder()
            .localDateTime(LocalDateTime.of(talkDate, talkTime))
            .name(mapToString(row.get(1)))
            .congregation(mapToString(row.get(2)))
            .outlineNumber(mapToInt(row.get(3)))
            .outlineTitle(mapToString(row.get(4)))
            .phoneNumber(mapToString(row.get(5)))
            .email(mapToString(row.get(6)))
            .outlineHasImages(Boolean.parseBoolean(mapToString(row.get(7))))
            .outlineHasVideo(Boolean.parseBoolean(mapToString(row.get(8))))
            .build();
    }
}
