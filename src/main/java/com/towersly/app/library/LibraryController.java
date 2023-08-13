package com.towersly.app.library;

import com.towersly.app.library.model.Shelf;
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

    @PostMapping("/addshelf")
    public Shelf creteShelf(@RequestBody Shelf shelf) {
        return libraryService.creteShelf(shelf);
    }

    @PostMapping("/addwork")
    public Work creteShelf(@RequestBody Work work) {
        return libraryService.createWork(work);
    }

}