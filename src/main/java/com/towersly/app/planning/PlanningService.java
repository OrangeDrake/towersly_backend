package com.towersly.app.planning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.towersly.app.planning.model.Distribution;
import com.towersly.app.planning.model.DistributionWithConnectionAndUserId;
import com.towersly.app.planning.model.DistributionWithProjectionAndUserId;
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
        UserWithIdAndNextDistributionRank userWithIdAndNextDistributionRank = userService.getUserWithIdAndNextDistributionRank();
        if (userWithIdAndNextDistributionRank == null) {
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
        if (createdDistribution == null) {
            log.warn("User: " + userId + "|distribution: " + distribution.getName() + " not  creted");
            return null;
        }
        log.info("User: " + userId + "|distribution: " + distribution.getName() + " creted");
        return createdDistribution;
    }

    public List<Distribution> getAllDistributions() {
        int userId = userService.getUserId();
        if (userId == 0) {
            log.warn("User: " + userId + "|shelves not received");
            return null;
        }
        List<Distribution> distributions = distributionDAO.readAllDistributions(userId);
        if (distributions == null) {
            log.warn("User: " + userId + "|distributions not received");
            return null;
        }
        return distributions;
    }

    public JsonNode addConnectedShelf(Long distributionId, String shelfName) {
        int userId = userService.getUserId();
        DistributionWithConnectionAndUserId distribution = distributionDAO.getDistributionWithConnectionAndUserId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        if (userId != userIdFromDistribution) {
            log.warn("User: " + userId + "|trying to write to Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + " was not creted");
            return null;
        }

        JsonNode connection = distribution.getConnection();
        if (connection == null) {
            String connectionJson = "{\n" +
                    "    \"shelves_names\" : [\"" + shelfName + "\"]\n" +
                    "}";
            try {
                connection = mapper.readTree(connectionJson);
            } catch (JsonProcessingException e) {
                log.warn("User: " + userId + "|jsonProcessingException");
                log.warn("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + " was not creted");
                return null;
            }

            distributionDAO.createConnection(distributionId, connectionJson);
            log.info("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + " was creted");
            return connection;
        }

        var fields = connection.fields();
        int numberofConections = 0;
        String typeOfconection = null;

        while (fields.hasNext()) {
            var field = fields.next();
            if (field.getKey().equals("shelves_names")) {
                var shelves_names = field.getValue();
                for (JsonNode shelf_name : shelves_names) {
                    numberofConections++;
                    if (shelf_name.asText().equals(shelfName)) {
                        log.warn("User: " + userId + "|shlelve name: " + shelfName + " is alredy connected in distribution: " + distributionId);
                        log.warn("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + "was not  creted");
                        return null;
                    }
                }
                var shelvesNamesArray = (ArrayNode) shelves_names;
                shelvesNamesArray.add(shelfName);
            } else if (field.getKey().equals("type")) {
                typeOfconection = field.getValue().asText();
            }
        }

        if (typeOfconection == null && numberofConections > 0) {
            ObjectNode connectionObject = (ObjectNode) connection;
            connectionObject.put("type", "concat");
        }

        distributionDAO.createConnection(distributionId, connection.toString());

        log.info("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + " was creted");
        return connection;
    }

    public void removeConnectedShelf(Long distributionId, String shelfName) {
        int userId = userService.getUserId();
        DistributionWithConnectionAndUserId distribution = distributionDAO.getDistributionWithConnectionAndUserId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        if (userId != userIdFromDistribution) {
            log.warn("User: " + userId + "|trying to remove in Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + " not  removed");
            return;
        }

        distributionDAO.deleteConnectedShelf(distributionId, shelfName);
        log.info("User: " + userId + "|connection to shelf: " + shelfName + " in distribution: " + distributionId + " removed");
    }

    public void changeConnectingType(Long distributionId, String type) {
        int userId = userService.getUserId();
        DistributionWithConnectionAndUserId distribution = distributionDAO.getDistributionWithConnectionAndUserId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        if (userId != userIdFromDistribution) {
            log.warn("User: " + userId + "|trying to remove in Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "|connecting type was not changed to: " + type + " in distribution: " + distributionId);
            return;
        }

        String typeJson = "\"" + type + "\"";
        distributionDAO.updateConnectingType(distributionId, typeJson);
        log.info("User: " + userId + "|connecting type was changed to: " + type + " in distribution: " + distributionId);
    }


    public JsonNode addRule(Long distributionId, JsonNode nRule) {
        int userId = userService.getUserId();
        DistributionWithProjectionAndUserId distribution = distributionDAO.getDistributionWithProjectionAndUserId(distributionId);
        int userIdFromDistribution = distribution.getUserId();

        var nRuleFields = nRule.fields();

        String nRuleName = "no name";

        while (nRuleFields.hasNext()) {
            var nRuleField = nRuleFields.next();
            if (nRuleField.getKey().equals("name")) {
                nRuleName = nRuleField.getValue().asText();
                break;
            }
        }

        if (userId != userIdFromDistribution) {
            log.warn("User: " + userId + "|trying to write to Distribution id: " + distributionId + ", User: " + userIdFromDistribution);
            log.warn("User: " + userId + "|rule: " + nRuleName + " in distribution: " + distributionId + " was not creted");
            return null;
        }


        JsonNode projection = distribution.getProjection();
        if (projection == null) {
            String projectionJson = "{\n" +
                    "    \"rules\" : []\n" +
                    "}";

            try {
                projection = mapper.readTree(projectionJson);
            } catch (JsonProcessingException e) {
                log.warn("User: " + userId + "|create Projection JsonProcessingException");
                log.warn("User: " + userId + "|rule: " + nRuleName + " in distribution: " + distributionId + " was not creted");
                return null;
            }
        }

        var projectionFields = projection.fields();
        while (projectionFields.hasNext()) {
            var projectionField = projectionFields.next();
            if (projectionField.getKey().equals("rules")) {
                var rulesArray = (ArrayNode) projectionField.getValue();
                rulesArray.add(nRule);
                log.info("User: " + userId + "|rule: " + nRuleName + " in distribution: " + distributionId + " was creted");
                break;
            }
        }
        distributionDAO.createProjection(distributionId, projection.toString());
        return projection;
    }
}
