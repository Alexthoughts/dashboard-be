package dashboard.controllers;

import dashboard.dto.HolidayFeDto;
import dashboard.services.HolidayService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/holiday")
@AllArgsConstructor
public class HolidayController {
    private HolidayService holidayService;

    @GetMapping("/get-holiday-list/{countryCode}")
    public List<HolidayFeDto> getHolidayList(@PathVariable("countryCode") String countryCode) {
        return holidayService.getHolidayList(countryCode);
    }
}
