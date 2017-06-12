package com.max.app.contextlearning.database;

import android.provider.BaseColumns;

public class DataSetDbContract {

    private DataSetDbContract() {}

    public static class RawEntries implements BaseColumns {
        public static final String TABLE_NAME = "RawDataSet";
        public static final String COLUMN_NAME_TIME = "Time";
        public static final String COLUMN_NAME_SENSORS = "Sensors";
    }

    public static class LabeledEntries implements BaseColumns {
        public static final String TABLE_NAME = "LabeledDataSet";
        public static final String COLUMN_NAME_SENSORS = "Sensors";
        public static final String COLUMN_NAME_LABEL = "Label";
    }

}
