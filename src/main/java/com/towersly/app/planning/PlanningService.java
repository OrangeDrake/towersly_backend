package com.towersly.app.planning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public JsonNode addConnectedShelf(Long distributionId, String shelfName){
        int userId  = userService.getUserId();
        DistributionWithConnectionAndUseId distribution = distributionDAO.getDistributionWithConnectionAndUseId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        if(userId != userIdFromDistribution){
            log.warn("User: " + userId + "| Trying to write to Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "| Connection to shelf: " + shelfName + " not  creted");
            return null;
        }

        JsonNode connection = distribution.getConnection();
        if(connection == null){
            String connectionText = "{\n" +
                    "    \"shelves_names\" : [\""+ shelfName + "\"]\n" +
                    "}";
            try {
                connection =  mapper.readTree(connectionText);
            } catch (JsonProcessingException e) {
                log.warn("User: " + userId + "| JsonProcessingException");
                log.warn("User: " + userId + "| Connection to shelf: " + shelfName + " not  creted");
                return null;
            }

            distributionDAO.createConnection(distributionId, connectionText);
            log.info("User: " + userId + "| Connection to shelf: " + shelfName + " creted");
            return connection;
        }

        var fields = connection.fields();
        int numberofConections = 0;
        String typeOfconection = null;

        while (fields.hasNext()){
            var field = fields.next();
            if (field.getKey().equals("shelves_names")){
                var shelves_names = field.getValue();
                for (JsonNode shelf_name : shelves_names){
                    numberofConections++;
                    if(shelf_name.asText().equals(shelfName)){
                        log.warn("User: " + userId + "| Shlelve name: " + shelfName + " is alredy connected ");
                        log.warn("User: " + userId + "| Connection to shelf: " + shelfName + " not  creted");
                        return null;
                    }
                }
                var shelvesNamesArray =  (ArrayNode) shelves_names;
                shelvesNamesArray.add(shelfName);
            }

            else if (field.getKey().equals("type")){
                typeOfconection = field.getValue().asText();
            }
        }

        if(typeOfconection == null || numberofConections > 0){
            ObjectNode connectionObject = (ObjectNode ) connection;
            connectionObject.put("type", "concat");
        }

        distributionDAO.createConnection(distributionId, connection.toString());

        log.info("User: " + userId + "| Connection to shelf: " + shelfName + " creted");
        return connection;

//        String shelfNameJson ="\"" + shelfName + "\"";
//        if(typeOfconection !=null || numberofConections == 0){
//        distributionDAO.addConnectedShelfwithType(distributionId,shelfNameJson);}
//        else{ //pokud je typ nastaven z minula nebo connection byly po predchozim mazani prazdne a ted je jedna o prvni conncetion nebude treba type vytvaret
//            distributionDAO.addConnectedShelfwithType(distributionId,shelfNameJson);
//        }
//        log.info("User: " + userId + "| Shlelve name: " + shelfName + " created ");
//        return true;
    }
    public void removeConnectedShelf(Long distributionId, String shelfName){
        int userId  = userService.getUserId();
        DistributionWithConnectionAndUseId distribution = distributionDAO.getDistributionWithConnectionAndUseId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        if(userId != userIdFromDistribution){
            log.warn("User: " + userId + "| Trying to remove in Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "| Connection to shelf: " + shelfName + " not  removed");
            return;
        }

        String shelfNameJson = "\"" + shelfName + "\"";
        distributionDAO.deleteConnectedShelf(distributionId, shelfName);
    }

}
