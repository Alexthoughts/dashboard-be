package com.dashboard.dashboard_be.controllers;

import com.dashboard.dashboard_be.services.HolidaysService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HolidayController {
    private HolidaysService holidaysService;

    @GetMapping("/getHolidayList")
    public String getHolidayList() {

        return holidaysService.getHolidayList();
    }
}
