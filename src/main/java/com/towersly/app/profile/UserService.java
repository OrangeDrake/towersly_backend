package com.towersly.app.profile;

import com.towersly.app.profile.model.UserWithIdAndNextDistributionRank;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Value("${library.shelf-rank-shift}")
    private int SHELF_RANK_SHIFT;
    @Value("${library.work-rank-shift}")
    private int WORK_RANK_SHIFT;

    private final int INIT_VISIBLE_WORKS = 5;

    private final UserDAO userDAO;

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

    public void creteUserProfileIfNeeded() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        int id = userDAO.getUserId(name);
        if (id == 0) {
            id = createNewProfile(name);
            log.info("User: " + id + " created");
            return;
        }
        log.info("User: " + id + " already exist");
    }

    private int createNewProfile(String name) {
        return userDAO.createNewProfile(name, SHELF_RANK_SHIFT, WORK_RANK_SHIFT, INIT_VISIBLE_WORKS);
    }

}
