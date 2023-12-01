package com.towersly.app.profile;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/profile")
@Slf4j
@AllArgsConstructor
public class TrackingController {

    private TrackingService trackingService;

    @PostMapping(value = "/startTracking")
    public void startTracking(@RequestBody JsonNode tracking){
        trackingService.startTracking(tracking);
    }

    @PostMapping(value = "/stopTracking")
    public int stopTracking(@RequestBody JsonNode stopTime){
        return trackingService.stopTracking(stopTime);
    }

    @GetMapping(value = "/tracking")
    public JsonNode getTracking(){
        return trackingService.getTracking();
    }
}