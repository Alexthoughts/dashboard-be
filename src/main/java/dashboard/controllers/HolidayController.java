package dashboard.controllers;

import dashboard.services.HolidayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/holiday")
public class HolidayController {
    private HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/get-holiday-list")
    public List getHolidayList() {
        return holidayService.getHolidayList();
    }
}
