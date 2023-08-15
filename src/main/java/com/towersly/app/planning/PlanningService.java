package com.towersly.app.planning;

import com.towersly.app.library.ShelfDAO;
import com.towersly.app.library.WorkDAO;
import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.Work;
import com.towersly.app.planning.model.Distribution;
import com.towersly.app.profile.DistributionDAO;
import com.towersly.app.profile.UserService;
import com.towersly.app.profile.model.UserWithIdAndNextDistributionRank;
import com.towersly.app.profile.model.UserWithIdAndNextShelfRank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PlanningService {

    private DistributionDAO distributionDAO;

    private UserService userService;

    public Distribution addDistribution(Distribution distribution) {
        UserWithIdAndNextDistributionRank userWithIdAndNextDistributionRank  = userService.getUserWithIdAndNextDistributionRank();
        if(userWithIdAndNextDistributionRank == null){
            log.info("Distribution: " + distribution.getName() + " not  creted");
            return null;
        }
        int rank = userWithIdAndNextDistributionRank.getNextDistributionRank();
        int userId = userWithIdAndNextDistributionRank.getId();
        distribution.setRank(rank++);
        userService.updateNextDistributionRank(userId, rank);
        distribution.setUserId(userId);
        distribution.setActive(true);
        Distribution createdDistribution = distributionDAO.create(distribution);
        if(createdDistribution == null){
            log.info("User: " + userId + "| Distribution: " + distribution.getName() + " not  creted");
            return null;
        }
        log.info("User: " + userId + "| Distribution: " + distribution.getName() + " creted");
        return createdDistribution;
    }
}
