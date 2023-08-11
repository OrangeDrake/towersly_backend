package com.towersly.app.library;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(path = "/library")
@AllArgsConstructor
public class LibraryController {

    @GetMapping
    public String allShelves(){
        return "funguje";
    }

}