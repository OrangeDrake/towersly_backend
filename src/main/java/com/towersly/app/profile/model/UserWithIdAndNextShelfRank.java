package com.towersly.app.profile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserWithIdAndNextShelfRank {

    private int id;
    private int nextShelfRank;
}
