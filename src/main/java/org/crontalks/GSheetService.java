package org.crontalks;

import com.google.api.services.sheets.v4.Sheets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GSheetService {

    private final String gSheetBook;
    private final Sheets sheets;

    public GSheetService(GSheetServiceAuthorize gSheetServiceAuthorize, Params.GSheets gSheets) throws IOException {
        this.gSheetBook = gSheets.getSheet();
        this.sheets = gSheetServiceAuthorize.authorize();
    }

    public List<List<Object>> getSheetValues(String sheet, String range, boolean filterEmpty) {
        return getSheetValues(sheet, range, filterEmpty, 0);
    }

    public List<List<Object>> getSheetValues(String sheet, String range, boolean filterEmpty, int columnIndex) {
        var values = Optional.ofNullable(sheetSnippet(sheet, range)).orElse(new ArrayList<>());

        //log.info("Sheet values: {}", values);

        if (filterEmpty)
            values = values.stream().filter(v -> v.size() - 1 >= columnIndex &&
                    StringUtils.isNotBlank(v.get(columnIndex).toString()))
                .toList();

        return values;
    }

    private List<List<Object>> sheetSnippet(String sheet, String range) {
        try {
            return sheets.spreadsheets().values()
                .get(gSheetBook, String.format("%s!%s", sheet, range))
                .execute()
                .getValues();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }
}
