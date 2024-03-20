package com.towersly.app.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CalendarDAO {

    private JdbcTemplate jdbcTemplate;

    public void createPlan(int yearAndWeekNumber, String planJson, int userId) {
        String sql = """
                   insert into public.calendar (year_and_weeknumber, plan, user_id) values (?, cast(? as jsonb), ?)
                   ON CONFLICT (year_and_weeknumber, user_id) DO UPDATE
                   SET plan = cast(? as jsonb)
                   """;
        jdbcTemplate.update(sql, yearAndWeekNumber, planJson, userId, planJson );
    }
}
