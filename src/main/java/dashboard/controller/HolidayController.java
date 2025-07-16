package dashboard.controller;

import dashboard.dto.fe.HolidayFeDto;
import dashboard.service.HolidayService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/holiday")
@AllArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping("/get-holiday-list/{countryCode}")
    public List<HolidayFeDto> getHolidayList(@PathVariable("countryCode") String countryCode) {
        return holidayService.getHolidayList(countryCode);
    }
}
