package org.crontalks.service;

import lombok.RequiredArgsConstructor;
import org.crontalks.constants.Params;
import org.crontalks.entity.ScheduledTalk;
import org.crontalks.mapper.ScheduledTalkMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final GSheetService gSheetService;

    public ScheduledTalk getCurrentScheduledTalk() {
        final String speakerSheetRange = "A1:H";
        var speakerThisWeek = gSheetService.getSheetValues(
            Params.GSheets.getInstance().getThisWeekSpeaker(), speakerSheetRange, true);

        if (speakerThisWeek.isEmpty())
            return null;

        return ScheduledTalkMapper.toScheduledTalk(speakerThisWeek.getFirst());
    }

}
