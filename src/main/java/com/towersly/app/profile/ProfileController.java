package com.towersly.app.profile;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/profile")
@Slf4j
@AllArgsConstructor
public class ProfileController {

    private UserService userService;

    @PostMapping(value = "/startTracking")
    public void startTracking(@RequestBody JsonNode tracking ){
        userService.startTracking(tracking);
    }

    @GetMapping(value = "/stopTracking")
    public void stopTracking(){
        userService.stopTracking();
    }

    @GetMapping(value = "/tracking")
    public JsonNode getTracking(){
        return userService.getTracking();
    }
}