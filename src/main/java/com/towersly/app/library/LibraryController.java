package com.towersly.app.library;

import com.towersly.app.library.model.Shelf;
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
    public void creteShelf(@RequestBody  Shelf shelf){
       libraryService.creteShelf(shelf);
    }

}