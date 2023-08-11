package com.towersly.app.library;

import com.towersly.app.library.model.Shelf;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryService {

    private LibraryDAO libraryDAO;

    public void creteShelf(Shelf shelf) {
        shelf.setUser_id(1);
        libraryDAO.create(shelf);
    }
}
