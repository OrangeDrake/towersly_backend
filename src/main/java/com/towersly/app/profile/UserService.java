package com.towersly.app.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.towersly.app.profile.model.UserWithIdAndNextDistributionRank;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    UserDAO userDAO;

    public int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        int id = userDAO.getUserId(name);
        if (id == 0) {
            log.warn("User: " + name + " not  fount");
        }
        return id;
    }

    public UserWithIdAndNextShelfRank getUserWithIdAndNextShelfRank() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserWithIdAndNextShelfRank userWithIdAndNextShelfRank = userDAO.getUserWithIdAndNextShelfRank(name);
        if (userWithIdAndNextShelfRank == null) {
            log.warn("User: " + name + " not  fount");
        }
        return userWithIdAndNextShelfRank;
    }

    public UserWithIdAndNextDistributionRank getUserWithIdAndNextDistributionRank() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserWithIdAndNextDistributionRank userWithIdAndNextDistributionRank = userDAO.getUserWithIdAndNextDistributionRank(name);
        if (userWithIdAndNextDistributionRank == null) {
            log.warn("User: " + name + " not  fount");
        }
        return userWithIdAndNextDistributionRank;
    }


    public void updateNextShelfRank(int id, int nextShelfRank) {
        userDAO.updateNextShelfRank(id, nextShelfRank);
    }

    public void updateNextDistributionRank(int id, int nextDistributionRank) {
        userDAO.updateNextDistributionfRank(id, nextDistributionRank);
    }
    public void startTracking(JsonNode tracking) {
        int id = getUserId();
        userDAO.startTracking(id, tracking.toString());
    }

    public void stopTracking() {
        int id = getUserId();
        userDAO.stopTracking(id);
    }

    public JsonNode getTracking() {
        int id = getUserId();
        return userDAO.readTracking(id);
    }
}
