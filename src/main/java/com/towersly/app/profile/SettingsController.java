package com.towersly.app.profile;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/settings")
@AllArgsConstructor
public class SettingsController {

    private SettingsService settingsService;

    @GetMapping(value = "/worksnumber")
    public int getNumberOfVisibleWorks() {
        return settingsService.getNumberOfVisibleWorks();
    }

    @GetMapping(value = "/setworksnumber")
    public void setNumberOfVisibleWorks(@RequestParam int numberOfVisibleWorks) {
       settingsService.setNumberOfVisibleWorks(numberOfVisibleWorks);
    }

}