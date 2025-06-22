package com.example.dashboard_be.controllers;

import com.example.dashboard_be.services.HolidayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HolidayController {
    private HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/get-holiday-list")
    public String getHolidayList() {
        return holidayService.getHolidayList();
    }
}
