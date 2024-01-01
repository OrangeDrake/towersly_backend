package com.towersly.app.library.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

@Getter
public class WorkUpdateRanks {

    private List<WorkPosition> workPositions;

    public WorkUpdateRanks() {
        this.workPositions = new LinkedList<>();
    }

    public void addWorkPosition(long id, int rank) {
        workPositions.add(new WorkPosition(id, rank));
    }

    

}
