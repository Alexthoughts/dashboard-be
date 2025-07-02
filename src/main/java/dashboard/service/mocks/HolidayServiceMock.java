package dashboard.service.mocks;

import dashboard.dto.HolidayResponseDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class HolidayServiceMock {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Date parse(String date) throws ParseException {
        return sdf.parse(date);
    }

    public List<HolidayResponseDto> getMockHolidayList() {

        try {
            return Arrays.asList(
                    new HolidayResponseDto(parse("2025-01-01"), "Den obnovy samostatného českého státu; Nový rok", "New Year's Day", "CZ", false, true),
                    new HolidayResponseDto(parse("2025-04-18"), "Velký pátek", "Good Friday", "CZ", false, true),
                    new HolidayResponseDto(parse("2025-01-01"), "Velikonoční pondělí", "Easter Monday", "CZ", false, true)
            );
        } catch (ParseException parseException) {
            throw new RuntimeException("Failed to convert date", parseException);
        }
    }
}
