package dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record HolidayFeDto(
        int id,
        String name,
        String localName,

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date date
) {

}
