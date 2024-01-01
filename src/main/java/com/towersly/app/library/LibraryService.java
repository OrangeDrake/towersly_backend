package com.towersly.app.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.Work;
import com.towersly.app.library.model.WorkPosition;
import com.towersly.app.library.model.WorkUpdateRanks;
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

    private final int WORK_RANK_SHIFT = 100;

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
            log.warn("User: " + userId + "| Trying to write to Shelf id: " + shelfWithIdAndNextWorkRankAndUserId.getId()
                    + ", User: " + userIdFromShelf);
            log.warn("User: " + userId + "|Work: " + work.getName() + " not  created");
            return null;
        }
        int rank = shelfWithIdAndNextWorkRankAndUserId.getNextWorkRank();
        work.setRank(rank);
        rank += WORK_RANK_SHIFT;
        shelfDAO.updateNextWorkfRank(shelfId, rank);
        Work createdWork = workDAO.create(work);

        log.info("User: " + userId + "| Work: " + work.getName() + " created");
        return createdWork;
    }

    public void savework(Work work, long workId) {
        int userId = userService.getUserId();
        if (userId == 0) {
            log.warn("Work: " + work.getName() + " not edited");
            return;
        }
        long shelfId = work.getShelfId();
        var shelfWithIdAndNextWorkRankAndUserId = shelfDAO.readShelfWithIdAndNextWorkRankAndUserId(shelfId);
        int userIdFromShelf = shelfWithIdAndNextWorkRankAndUserId.getUserId();
        if (userId != userIdFromShelf) {
            log.warn("User: " + userId + "| Trying to write to Shelf id: " + shelfWithIdAndNextWorkRankAndUserId.getId()
                    + ", User: " + userIdFromShelf);
            log.warn("Work: " + work.getName() + " not  edited");
            return;
        }
        workDAO.update(work, workId);
        log.info("User: " + userId + "| Work: " + work.getName() + " edited");
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

    private boolean extractWorkPositionFromJson(JsonNode work, WorkUpdateRanks workUpdateRanks, int userId) {
        var workFields = work.fields();
        long workId = -1;
        int workRank = -1;
        while (workFields.hasNext()) {
            var workField = workFields.next();
            if (workField.getKey().equals(("id"))) {
                workId = workField.getValue().asLong();
            }
            if (workField.getKey().equals(("rank"))) {
                workRank = workField.getValue().asInt();
            }
        }
        if (workId == -1 || workRank == -1) {
            log.warn("User: " + userId + "| workId or workRank is missing");
            log.warn("User: " + userId + "|Works not updated");
            return false;
        }
        workUpdateRanks.addWorkPosition(workId, workRank);
        return true;
    }

    private WorkPosition getWorkPositionFromJson(JsonNode work, int userId) {
         var workFields = work.fields();
        long workId = -1;
        int workRank = -1;
        while (workFields.hasNext()) {
            var workField = workFields.next();
            if (workField.getKey().equals(("id"))) {
                workId = workField.getValue().asLong();
            }
            if (workField.getKey().equals(("rank"))) {
                workRank = workField.getValue().asInt();
            }
        }
        if (workId == -1 || workRank == -1) {
            log.warn("User: " + userId + "| workId or workRank is missing");
            log.warn("User: " + userId + "|Works not updated");
            return null;
        }
        return new WorkPosition(workId, workRank);
    }

    public void updateWorks(JsonNode worksUpdate) {

        int userId = userService.getUserId();
        if (userId == 0) {
            log.warn("User: " + userId + "|Works not updated");
            return;
        }

        var worksUpdateFields = worksUpdate.fields();

        long shelfId = -1;
        int worksMaxRank = -1;
        JsonNode workNewInShelf = null;
        WorkUpdateRanks workUpdateRanks = new WorkUpdateRanks();

        while (worksUpdateFields.hasNext()) {
            var workUpdateField = worksUpdateFields.next();
            if (workUpdateField.getKey().equals("shelfId")) {
                shelfId = workUpdateField.getValue().asLong();
            }
            if (workUpdateField.getKey().equals("maxRank")) {
                worksMaxRank = workUpdateField.getValue().asInt();
            }
            if (workUpdateField.getKey().equals("works")) {
                var works = workUpdateField.getValue();
                for (JsonNode work : works) {
                    if (!extractWorkPositionFromJson(work, workUpdateRanks, userId)) {
                        return;
                    }
                }
            }
            if (workUpdateField.getKey().equals("workNewInShelf")) {
                workNewInShelf = workUpdateField.getValue();
            }
        }

        if (shelfId == -1 || worksMaxRank == -1) {
            log.warn("User: " + userId + "| shelfId or workMaxRank is missing");
            log.warn("User: " + userId + "|Works not updated");
            return;
        }

        var shelfWithIdAndNextWorkRankAndUserId = shelfDAO.readShelfWithIdAndNextWorkRankAndUserId(shelfId);
        int userIdFromShelf = shelfWithIdAndNextWorkRankAndUserId.getUserId();
        if (userId != userIdFromShelf) {
            log.warn("User: " + userId + "| Trying to write to Shelf id: " + shelfWithIdAndNextWorkRankAndUserId.getId()
                    + ", User: " + userIdFromShelf);
            log.warn("User: " + userId + "|Works not  updated");
            return;
        }

        if (worksMaxRank >= shelfWithIdAndNextWorkRankAndUserId.getNextWorkRank()) {
            shelfDAO.updateNextWorkfRank(shelfId, worksMaxRank + WORK_RANK_SHIFT);
        }

        if (workNewInShelf != null) {
            WorkPosition workPositionWithShelf = null;
            workPositionWithShelf = getWorkPositionFromJson(workNewInShelf, userId);
            if (workPositionWithShelf == null) {
                return;
            }
            workDAO.updateWorksWithShelf(workPositionWithShelf, shelfId);
        }

        workDAO.updateWorks(workUpdateRanks);
        log.info("User: " + userId + "| Works updated");
    }

    public int addDurationToWork(int userId, long workId, int durationInSeconds, long shelfId) {
        int actualTime = workDAO.updateActualTime(workId, durationInSeconds, shelfId);
        log.info("User: " + userId + "| Duration: " + durationInSeconds + " added to Work: " + workId
                + ", actual time is: " + actualTime);
        return actualTime;
    }
}
