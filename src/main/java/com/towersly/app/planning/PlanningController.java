package com.towersly.app.planning;

import com.towersly.app.library.LibraryService;
import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.Work;
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
    public boolean addConnectedShelf(@RequestParam long distributionId, @RequestParam  String shelfName){
        return planningService.addConnectedShelf(distributionId, shelfName);
    }


}