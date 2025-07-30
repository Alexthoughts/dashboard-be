package dashboard.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class HelperMethods {
    public LocalDateTime convertEpochSecondsToLocalDateTime(Long epochSeconds) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.systemDefault()
        );
    }

    public String roundTwoDecimalsAndConvertToString(Double value) {
        return String.format("%.2f", value);
    }
}
