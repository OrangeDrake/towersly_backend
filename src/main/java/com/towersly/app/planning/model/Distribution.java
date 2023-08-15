package com.towersly.app.planning.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Distribution {

    private Long id;
    private boolean isActive;
    private String name;
    private int rank;

    private JsonNode connection;

    private JsonNode projection;

    private int userId;

}
