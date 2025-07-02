package dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HolidayResponseDto(
        Date date,
        String localName,
        String name,
        String countryCode,
        Boolean fixed,
        Boolean global
) {
}
