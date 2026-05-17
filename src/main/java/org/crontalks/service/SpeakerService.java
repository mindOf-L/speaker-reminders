package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.GSheetsProperties;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.mapper.ScheduledTalkMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final GSheetService gSheetService;
    private final GSheetsProperties gSheetsProperties;
    private final ScheduledTalkMapper scheduledTalkMapper;

    public ScheduledTalk getCurrentScheduledTalk() {
        final String speakerSheetRange = "A1:J1";
        return getScheduledTalk(speakerSheetRange);
    }

    public ScheduledTalk getNext4WeekScheduledTalk() {
        final String speakerSheetRange = "A2:J2";
        return getScheduledTalk(speakerSheetRange);
    }

    private ScheduledTalk getScheduledTalk(String speakerSheetRange) {
        var speakerThisWeek = gSheetService.getSheetValues(
            gSheetsProperties.speakerSheet(), speakerSheetRange, true);

        if (speakerThisWeek.isEmpty())
            return null;

        return scheduledTalkMapper.toScheduledTalk(speakerThisWeek.getFirst());
    }

}
