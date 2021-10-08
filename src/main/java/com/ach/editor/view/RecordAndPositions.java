package com.ach.editor.view;

import com.ach.domain.ACHRecord;
import com.ach.domain.ACHRecordFileHeader;

public class RecordAndPositions {
   private final ACHRecord achRecord;
    private final Integer[] integers;

    public RecordAndPositions(ACHRecord achRecord, Integer[] integers) {
        this.achRecord = achRecord;
        this.integers = integers;
    }

    public Integer[] getIntegers() {
        return integers;
    }

    public ACHRecord getAchRecord() {
        return achRecord;
    }
}
