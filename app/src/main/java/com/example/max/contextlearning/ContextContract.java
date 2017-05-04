package com.example.max.contextlearning;

import android.provider.BaseColumns;

/**
 * Created by Max on 3/5/2017.
 */

public class ContextContract {

    private ContextContract() {}

    public static class VolumeEntry implements BaseColumns {
        public static final String TABLE_NAME = "volumeKB";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_NOISE = "noise";
        public static final String COLUMN_NAME_VOLUME = "volume";
    }

    public static class TrainingSet implements BaseColumns {
        public static final String TABLE_NAME = "TrainingSet";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_LIGHT = "light";
        public static final String COLUMN_NAME_PROXIMITY = "proximity";
        public static final String COLUMN_NAME_NOISE = "noise";
        public static final String COLUMN_NAME_GRAVITY = "gravity";
        public static final String COLUMN_NAME_ACCELERATION = "acceleration";
        public static final String COLUMN_NAME_DEVICE_TMP = "device_tmp";
    }
}
