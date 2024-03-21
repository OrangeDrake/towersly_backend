package com.towersly.app.calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towersly.app.planning.model.Distribution;
import com.towersly.app.planning.model.DistributionWithConnectionAndUserId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class CalendarDAO {

    private JdbcTemplate jdbcTemplate;
    final private ObjectMapper mapper = new ObjectMapper();

    public void createPlan(int yearAndWeekNumber, String planJson, int userId) {
        String sql = """
                insert into public.calendar (year_and_weeknumber, plan, user_id) values (?, cast(? as jsonb), ?)
                ON CONFLICT (year_and_weeknumber, user_id) DO UPDATE
                SET plan = cast(? as jsonb)
                """;
        jdbcTemplate.update(sql, yearAndWeekNumber, planJson, userId, planJson);
    }

    public JsonNode readPlan(int yearAndWeekNumber, int userId) {
        System.out.println("id " + userId);
        System.out.println("yearAndWeekNumber " + yearAndWeekNumber);
        String sql = "select plan from public.calendar where user_id = ? and year_and_weeknumber = ?";

        return jdbcTemplate.queryForObject(sql, ((rs, row) -> {
            System.out.println();
            String planJson = rs.getString("plan");
            try {
                return mapper.readTree(planJson);
            } catch (JsonProcessingException e) {
                log.error("Plan of user: " + userId + ", " + "yearAndWeekNumber: " + yearAndWeekNumber + "|Plan json parsing error");
                return null;
            }
        }), userId, yearAndWeekNumber);

    }
}
