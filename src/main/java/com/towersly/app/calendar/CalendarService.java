package com.towersly.app.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.towersly.app.planning.model.Distribution;
import com.towersly.app.profile.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CalendarService {

    private UserService userService;
    private CalendarDAO calendarDAO;

    public void addPlan(int yearAndWeekNumber, JsonNode plan) {

        int userId = userService.getUserId();
        calendarDAO.createPlan(yearAndWeekNumber, plan.toString(), userId );
    }
}
