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

//    @GetMapping
//    public Iterable<ShelfContainingWorks> getAllShelves(){
//        var result = libraryService.getAllShelves();
//        return  result;
//    }
    @PostMapping("/adddistribution")
    public Distribution addDistribution(@RequestBody Distribution distribution) {
        return planningService.addDistribution(distribution);
    }

}