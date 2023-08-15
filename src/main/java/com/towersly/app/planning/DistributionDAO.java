package com.towersly.app.planning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.towersly.app.library.model.Shelf;
import com.towersly.app.library.model.ShelfContainingWorks;
import com.towersly.app.library.model.ShelfWithIdAndNextWorkRankAndUserId;
import com.towersly.app.library.model.Work;
import com.towersly.app.planning.model.Distribution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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

        String connectionText = null;
        JsonNode connection = distribution.getConnection();
        if (connection != null) {
            connectionText = connection.toString();
        }
        String projectionText = null;
        JsonNode projection = distribution.getProjection();
        if (projection != null) {
            projectionText = projection.toString();
        }

        var psc = pscf.newPreparedStatementCreator(Arrays.asList(distribution.getName(), distribution.isActive(), distribution.getRank(), connectionText, projectionText, distribution.getUserId()));
        jdbcTemplate.update(psc, generatedKeyHolder);
        var id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        distribution.setId(id);
        distribution.setUserId(0);
        return distribution;
    }

    public List<Distribution> readAllDistributions(int userId) {
        List<Distribution> distributions = null;
        String sql = "select id, is_active, name, rank, connection, projection from public.distribution where user_id = ?";

        distributions = jdbcTemplate.query(sql, new Object[]{userId}, ((rs, rowNum) -> {

            JsonNode connection = null;
            String connectionText = rs.getString("connection");

            try {
                if (connectionText != null) {
                    connection = mapper.readTree(connectionText);
                }
            } catch (JsonProcessingException e) {
                log.error("User: " + userId + "| Distrubution Connection json parsing error");
                return null;
            }

            JsonNode projection = null;
            String projectionText = rs.getString("projection");

            try {
                if (projectionText != null) {
                    projection = mapper.readTree(projectionText);
                }
            } catch (JsonProcessingException e) {
                log.error("User: " + userId + "| Distrubution Projection json parsing error");
                return null;
            }

            return new Distribution(rs.getLong("id"), rs.getBoolean("is_active"), rs.getString("name"), rs.getInt("rank"), connection, projection, 0);

        }));

        return distributions;
    }

//
}

