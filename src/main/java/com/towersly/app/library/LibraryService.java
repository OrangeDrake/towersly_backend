package com.towersly.app.library;

import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.Work;
import com.towersly.app.profile.UserService;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryService {

    private ShelfDAO shelfDAO;
    private WorkDAO workDAO;
    private UserService userService;

    public ShelfContainingWorks addShelf(Shelf shelf) {
        UserWithIdAndNextShelfRank userWithIdAndNextShelfRank = userService.getUserWithIdAndNextShelfRank();
        if (userWithIdAndNextShelfRank == null) {
            log.warn("Shelf: " + shelf.getName() + " not  created");
            return null;
        }
        int rank = userWithIdAndNextShelfRank.getNextShelfRank();
        int userId = userWithIdAndNextShelfRank.getId();
        shelf.setRank(rank++);
        userService.updateNextShelfRank(userId, rank);
        shelf.setUserId(userId);
        shelf.setActive(true);
        ShelfContainingWorks createdShelf = shelfDAO.create(shelf);
        if (createdShelf == null) {
            log.warn("User: " + userId + "| Shelf: " + shelf.getName() + " not created");
            return null;
        }
        log.info("User: " + userId + "| Shelf: " + shelf.getName() + " created");
        return createdShelf;
    }

    public Work createWork(Work work) {
        int userId = userService.getUserId();
        if (userId == 0) {
            log.warn("Work: " + work.getName() + " not created");
            return null;
        }
        long shelfId = work.getShelfId();
        var shelfWithIdAndNextWorkRankAndUserId = shelfDAO.readShelfWithIdAndNextWorkRankAndUserId(shelfId);
        int userIdFromShelf = shelfWithIdAndNextWorkRankAndUserId.getUserId();
        if (userId != userIdFromShelf) {
            log.warn("User: " + userId + "| Trying to write to Shelf id: " + shelfWithIdAndNextWorkRankAndUserId.getId() + ", User: " + userIdFromShelf);
            log.warn("Work: " + work.getName() + " not  created");
            return null;
        }
        int rank = shelfWithIdAndNextWorkRankAndUserId.getNextWorkRank();
        work.setRank(rank++);
        shelfDAO.updateNextWorkfRank(shelfId, rank);
        Work createdWork = workDAO.create(work);

        log.info("User: " + userId + "| Work: " + work.getName() + " created");
        return createdWork;
    }

    public List<ShelfContainingWorks> getAllShelves() {
        int userId = userService.getUserId();
        if (userId == 0) {
            log.warn("Shelves not received");
            return null;
        }
        List<ShelfContainingWorks> shelves = shelfDAO.readAllShelves(userId);
        if (shelves == null) {
            log.warn("User: " + userId + "| Shelves not received");
            return null;
        }
        return shelves;
    }

    public int addDurationToWork(int userId, long workId, int durationInSeconds, long shelfId) {
//        ShelfWithUserId shelfWithUserId = shelfDAO.readShelfWithUserIdContainingWork(workId);
//        if (shelfWithUserId.getUserId() != userId) {
//            log.warn("User: " + userId + "| Trying to write to Work with id: " + workId + ", User: " + shelfWithUserId.getUserId());
//            log.warn("Duration to Work with : " + workId + " not  not added");
//            return;
//        }
        int actualTime = workDAO.updateActualTime(workId, durationInSeconds, shelfId);
        log.info("User: " + userId + "| Duration: " + durationInSeconds + " added to Work: " + workId + ", actual time is: " + actualTime);
        return actualTime;
    }

    //public
}
