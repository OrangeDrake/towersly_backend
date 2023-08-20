package com.towersly.app.planning;

import com.fasterxml.jackson.databind.JsonNode;
import com.towersly.app.planning.model.Distribution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/planning")
@Slf4j
@AllArgsConstructor
public class PlanningController {

    private PlanningService planningService;

    @GetMapping
    public Iterable<Distribution> getAllDistributions(){
        return planningService.getAllDistributions();
    }
    @PostMapping("/adddistribution")
    public Distribution addDistribution(@RequestBody Distribution distribution) {
        return planningService.addDistribution(distribution);
    }

//    @GetMapping(value = "/addconnectedshelf")
//    public void addConnectedShelf(@RequestParam long distributionId, @RequestParam  String shelfName){
//        planningService.addConnectedShelf(distributionId, shelfName);
//    }

    @GetMapping(value = "/addconnectedshelf")
    public JsonNode addConnectedShelf(@RequestParam long distributionId, @RequestParam  String shelfName){
        return planningService.addConnectedShelf(distributionId, shelfName);
    }

    @GetMapping(value = "/removeconnectedshelf")
    public void removeConnectedShelf(@RequestParam long distributionId, @RequestParam  String shelfName){
        planningService.removeConnectedShelf(distributionId, shelfName);
    }

    @GetMapping(value = "/changeconnectingtype")
    public void changeConnectingType(@RequestParam long distributionId, @RequestParam  String type){
        planningService.changeConnectingType(distributionId, type);
    }

    @PostMapping(value = "/addRule")
    public JsonNode addRule(@RequestParam long distributionId, @RequestBody JsonNode rule ){
        return planningService.addRule(distributionId, rule);
    }







}