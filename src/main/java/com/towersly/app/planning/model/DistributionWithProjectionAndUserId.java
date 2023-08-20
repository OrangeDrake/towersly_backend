package com.towersly.app.planning.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DistributionWithProjectionAndUserId {

    private JsonNode projection;
    private int userId;
}
