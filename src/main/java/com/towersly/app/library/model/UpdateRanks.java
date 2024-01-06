package com.towersly.app.library.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

@Getter
public class UpdateRanks {

    private List<Position> positions;

    public UpdateRanks() {
        this.positions = new LinkedList<>();
    }

    public void addPosition(long id, int rank) {
        positions.add(new Position(id, rank));
    }

    

}
