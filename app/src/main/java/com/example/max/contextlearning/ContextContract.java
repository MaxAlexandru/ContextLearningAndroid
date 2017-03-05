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
}
