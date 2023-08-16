package com.towersly.app.planning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.planning.model.Distribution;
import com.towersly.app.planning.model.DistributionWithConnectionAndUseId;
import com.towersly.app.profile.UserService;
import com.towersly.app.profile.model.UserWithIdAndNextDistributionRank;
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

    final private ObjectMapper mapper = new ObjectMapper();

    public Distribution addDistribution(Distribution distribution) {
        UserWithIdAndNextDistributionRank userWithIdAndNextDistributionRank  = userService.getUserWithIdAndNextDistributionRank();
        if(userWithIdAndNextDistributionRank == null){
            log.warn("Distribution: " + distribution.getName() + " not  creted");
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
            log.warn("User: " + userId + "| Distribution: " + distribution.getName() + " not  creted");
            return null;
        }
        log.info("User: " + userId + "| Distribution: " + distribution.getName() + " creted");
        return createdDistribution;
    }

    public List<Distribution> getAllDistributions(){
        int userId  = userService.getUserId();
        if(userId == 0){
            log.warn("User: " + userId + "|Shelves not received");
            return null;
        }
        List<Distribution> distributions = distributionDAO.readAllDistributions(userId);
        if(distributions == null){
            log.warn("User: " + userId + "| Distributions not received");
            return null;
        }
        return distributions;
    }

    public boolean addConnectedShelf(Long distributionId, String shelfName){
        int userId  = userService.getUserId();
        DistributionWithConnectionAndUseId distribution = distributionDAO.getDistributionWithConnectionAndUseId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        if(userId != userIdFromDistribution){
            log.warn("User: " + userId + "| Trying to write to Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "| Connection to shelf: " + shelfName + " not  creted");
            return false;
        }

        JsonNode connection = distribution.getConnection();
        if(connection == null){
            String connectionText = "{\n" +
                    "    \"shelves_names\" : [\""+ shelfName + "\"]\n" +
                    "}";
            distributionDAO.createConnection(distributionId, connectionText);
            return true;
        }

        var fields = connection.fields();
        while (fields.hasNext()){
            var field = fields.next();
            if (field.getKey().equals("shelves_names")){
                var shelves_names = field.getValue();
                for (JsonNode shelf_name : shelves_names){
                    if(shelf_name.asText().equals(shelfName)){
                        log.warn("User: " + userId + "| Shlelve name: " + shelfName + " is alredy connected ");
                        log.warn("User: " + userId + "| Connection to shelf: " + shelfName + " not  creted");
                        return false;
                    }
                }

            }
            break;
        }

        String shelfNameJson ="\"" + shelfName + "\"";
        distributionDAO.addConnectedShelf(distributionId,shelfNameJson);
        log.info("User: " + userId + "| Shlelve name: " + shelfName + " created ");
        return true;
    }
}
