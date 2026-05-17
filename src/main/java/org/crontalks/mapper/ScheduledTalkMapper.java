package org.crontalks.mapper;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.SchedulingProperties;
import org.crontalks.entity.ScheduledTalk;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.crontalks.mapper.OptionalMapper.mapToBoolean;
import static org.crontalks.mapper.OptionalMapper.mapToInt;
import static org.crontalks.mapper.OptionalMapper.mapToString;

@Component
@RequiredArgsConstructor
public class ScheduledTalkMapper {

    private final SchedulingProperties schedulingProperties;

    public ScheduledTalk toScheduledTalk(List<Object> row) {
        var talkDate = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(row.getFirst().toString()));
        var talkTime = LocalTime.from(DateTimeFormatter.ofPattern("HH:mm").parse(schedulingProperties.getMeetingTime()));

        return ScheduledTalk.builder()
            .localDateTime(LocalDateTime.of(talkDate, talkTime))
            .name(mapToString(row.get(1)))
            .congregation(mapToString(row.get(2)))
            .outlineNumber(mapToInt(row.get(3)))
            .outlineTitle(mapToString(row.get(4)))
            .phoneNumber(mapToString(row.get(5)))
            .email(mapToString(row.get(6)))
            .outlineHasImages(mapToBoolean(row.get(7)))
            .outlineHasVideo(mapToBoolean(row.get(8)))
            .hasWhatsApp(mapToBoolean(row.get(9)))
            .build();
    }
}
