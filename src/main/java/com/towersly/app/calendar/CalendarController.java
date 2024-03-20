package com.towersly.app.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.tools.jconsole.JConsoleContext;
import com.towersly.app.planning.PlanningService;
import com.towersly.app.planning.model.Distribution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/calendar")
@Slf4j
@AllArgsConstructor
public class CalendarController {

    private CalendarService calendarService;



    @PostMapping("/addplan")
    public void addPlan(@RequestParam int yearAndWeekNumber, @RequestBody JsonNode plan) {
        System.out.println(plan);
        calendarService.addPlan(yearAndWeekNumber, plan);
    }

//    @GetMapping(value = "/addconnectedshelf")
//    public JsonNode addConnectedShelf(@RequestParam long distributionId, @RequestParam String shelfName) {
//        return planningService.addConnectedShelf(distributionId, shelfName);
//    }




}