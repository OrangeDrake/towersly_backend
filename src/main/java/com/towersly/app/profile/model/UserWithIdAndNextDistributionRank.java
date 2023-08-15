package com.towersly.app.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserWithIdAndNextDistributionRank {

    private int id;
    private int nextDistributionRank;
}
