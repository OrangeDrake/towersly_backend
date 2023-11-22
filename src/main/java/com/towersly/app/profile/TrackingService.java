package com.towersly.app.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.towersly.app.library.LibraryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class TrackingService {

    UserDAO userDAO;
    UserService userService;
    LibraryService libraryService;


    public void startTracking(JsonNode tracking) {
        int userId = userService.getUserId();
        userDAO.startTracking(userId, tracking.toString());
    }

    public void stopTracking(JsonNode stopTime) {
        int userId = userService.getUserId();


        long stop = -1L;
        var fields = stopTime.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            if (field.getKey().equals("stop")) {
                stop = field.getValue().asLong();
            }
        }

        JsonNode tracking = userDAO.readTracking(userId);
        long start = -1L;
        long workId = -1L;
        fields = tracking.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            if (field.getKey().equals("start")) {
                start = field.getValue().asLong();
            }
            if (field.getKey().equals("workId")) {
                workId = field.getValue().asLong();
            }
        }

        userDAO.updateTrackingToNUll(userId);
        log.info("User: " + userId + "| tracking  stopped");

        if (stop == -1L || start == -1L || workId == -1L ){
            log.warn("User: " + userId + "| stop time, start time or workId is missing");
            log.warn("User: " + userId + "| no duration added ");
            return;
        }

        int durationInSeconds = (int)(stop - start) / 1000;
        libraryService.addDurationToWork(userId, workId, durationInSeconds);
    }

    public JsonNode getTracking() {
        int userId = userService.getUserId();
        return userDAO.readTracking(userId);
    }
}
