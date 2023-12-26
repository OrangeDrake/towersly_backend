package com.towersly.app.library;

import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.Work;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/library")
@Slf4j
@AllArgsConstructor
public class LibraryController {

    private LibraryService libraryService;

    @GetMapping
    public Iterable<ShelfContainingWorks> getAllShelves(){
        var result = libraryService.getAllShelves();
        return result;
    }
    @PostMapping("/addshelf")
    public ShelfContainingWorks addShelf(@RequestBody Shelf shelf) {
        return libraryService.addShelf(shelf);
    }

    @PostMapping("/addwork")
    public Work addWork(@RequestBody Work work) {
        return libraryService.createWork(work);
    }

    @PostMapping("/savework")
    public void savework(@RequestBody Work work, @RequestParam long workId) {
        libraryService.savework(work, workId);
    }
}