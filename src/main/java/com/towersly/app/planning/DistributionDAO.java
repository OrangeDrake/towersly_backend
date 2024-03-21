package com.towersly.app.planning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towersly.app.planning.model.Distribution;
import com.towersly.app.planning.model.DistributionWithConnectionAndUserId;
import com.towersly.app.planning.model.DistributionWithProjectionAndUserId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class DistributionDAO {

    private JdbcTemplate jdbcTemplate;

    final private GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    final private ObjectMapper mapper = new ObjectMapper();

    public Distribution create(Distribution distribution) {

        String sql = "insert into public.distribution (name, is_active, rank, connection, projection, user_id) values (?, ?, ?, ?, ?, ?)";

        var decParams = Arrays.asList(new SqlParameter(Types.VARCHAR, "name"), new SqlParameter(Types.BOOLEAN, "is_active"),
                new SqlParameter(Types.INTEGER, "rank"), new SqlParameter(Types.OTHER, "connection"), new SqlParameter(Types.OTHER, "projection"), new SqlParameter(Types.INTEGER, "user_id"));
        var pscf = new PreparedStatementCreatorFactory(sql, decParams) {
            {
                setReturnGeneratedKeys(true);
                setGeneratedKeysColumnNames("id");
            }
        };

        //neni nutny novy connection i distribution je vzdy null
        String connectionJson = null;
        JsonNode connection = distribution.getConnection();
        if (connection != null) {
            connectionJson = connection.toString();
        }
        String projectionJson = null;
        JsonNode projection = distribution.getProjection();
        if (projection != null) {
            projectionJson = projection.toString();
        }

        var psc = pscf.newPreparedStatementCreator(Arrays.asList(distribution.getName(), distribution.isActive(), distribution.getRank(), connectionJson, projectionJson, distribution.getUserId()));
        jdbcTemplate.update(psc, generatedKeyHolder);
        var id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        distribution.setId(id);
        distribution.setUserId(0);
        return distribution;
    }

    public List<Distribution> readAllDistributions(int userId) {
        List<Distribution> distributions = null;
        String sql = "select id, is_active, name, rank, connection, projection from public.distribution where user_id = ?";

        distributions = jdbcTemplate.query(sql, ((rs, rowNum) -> {
            JsonNode connection = null;
            String connectionJSon = rs.getString("connection");

            try {
                if (connectionJSon != null) {
                    connection = mapper.readTree(connectionJSon);
                }
            } catch (JsonProcessingException e) {
                log.error("User: " + userId + "| Distrubution Connection json parsing error");
                return null;
            }

            JsonNode projection = null;
            String projectionJson = rs.getString("projection");

            try {
                if (projectionJson != null) {
                    projection = mapper.readTree(projectionJson);
                }
            } catch (JsonProcessingException e) {
                log.error("User: " + userId + "| Distrubution Projection json parsing error");
                return null;
            }

            return new Distribution(rs.getLong("id"), rs.getBoolean("is_active"), rs.getString("name"), rs.getInt("rank"), connection, projection, 0);

        }), userId);
        return distributions;
    }

//    public void addConnectedShelf(Long id, String shelfNameJson) {
//        String sql = "UPDATE Distribution SET connection = jsonb_insert(connection, '{shelves_names,-1}',cast(? as jsonb), true) WHERE id = ?";
//        jdbcTemplate.update(sql, shelfNameJson  , id);
//    }
//
//    public void addConnectedShelfwithType(Long id, String shelfNameJson) {
//        String sql = "UPDATE Distribution SET connection = jsonb_insert(jsonb_insert(connection, '{shelves_names,-1}',cast(? as jsonb), true), '{type}','\"concat\"') WHERE id = ?";
//        jdbcTemplate.update(sql, shelfNameJson  , id);
//    }

    public void deleteConnectedShelf(Long id, String shelfName) {
        String sql = "UPDATE Distribution SET connection = jsonb_set(connection, '{shelves_names}',(connection->'shelves_names') - ?, true) WHERE id = ?";
        jdbcTemplate.update(sql, shelfName, id);
    }

    public void updateConnectingType(Long id, String type) {
        String sql = "UPDATE distribution SET connection = jsonb_set(connection, '{type}',cast(? as jsonb), true) WHERE id = ?";
        jdbcTemplate.update(sql, type, id);
    }

    public DistributionWithConnectionAndUserId getDistributionWithConnectionAndUserId(long id) {
        DistributionWithConnectionAndUserId distributions = null;
        String sql = "select connection, user_id from public.distribution where id = ?";

        distributions = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> {

            JsonNode connection = null;
            String connectionJson = rs.getString("connection");
            try {
                if (connectionJson != null) {
                    connection = mapper.readTree(connectionJson);
                }
            } catch (JsonProcessingException e) {
                log.error("Distribution: " + id + "|Distrubution Connection json parsing error");
                return null;
            }

            return new DistributionWithConnectionAndUserId(connection, rs.getInt("user_id"));

        }), id);
        return distributions;
    }

    public DistributionWithProjectionAndUserId getDistributionWithProjectionAndUserId(Long id) {
        DistributionWithProjectionAndUserId distributions = null;
        String sql = "select projection, user_id from public.distribution where id = ?";

        distributions = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> {

            JsonNode projection = null;
            String projectionJson = rs.getString("projection");
            try {
                if (projectionJson != null) {
                    projection = mapper.readTree(projectionJson);
                }
            } catch (JsonProcessingException e) {
                log.error("Distribution: " + id + "| Distrubution Projection json parsing error");
                return null;
            }

            return new DistributionWithProjectionAndUserId(projection, rs.getInt("user_id"));

        }), id);
        return distributions;
    }

    public void createConnection(Long id, String connectionJson) {
        String sql = "UPDATE Distribution SET connection = cast(? as jsonb) WHERE id = ?";
        jdbcTemplate.update(sql, connectionJson, id);
    }

    public void createProjection(Long id, String projectionJson) {
        String sql = "UPDATE Distribution SET projection = cast(? as jsonb) WHERE id = ?";
        jdbcTemplate.update(sql, projectionJson, id);
    }
}