package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.mapper.ScheduledTalkMapper;
import org.springframework.stereotype.Service;

import static org.crontalks.constants.Params.GSheets.getGSheetsParam;

@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final GSheetService gSheetService;

    public ScheduledTalk getCurrentScheduledTalk() {
        final String speakerSheetRange = "A1:H";
        var speakerThisWeek = gSheetService.getSheetValues(
            getGSheetsParam().getThisWeekSpeaker(), speakerSheetRange, true);

        if (speakerThisWeek.isEmpty())
            return null;

        return ScheduledTalkMapper.toScheduledTalk(speakerThisWeek.getFirst());
    }

}
