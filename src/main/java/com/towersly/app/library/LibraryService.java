package com.towersly.app.library;

import com.towersly.app.library.model.Shelf;
import com.towersly.app.profile.UserService;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryService {

    private LibraryDAO libraryDAO;

    private UserService userService;

    public Shelf creteShelf(Shelf shelf) {
        UserWithIdAndNextShelfRank userWithIdAndNextShelfRank  = userService.getUserWithIdAndNextShelfRank();
        shelf.setUser_id(userWithIdAndNextShelfRank.getId());
        shelf.set_active(true);
        return libraryDAO.create(shelf);
    }
}
