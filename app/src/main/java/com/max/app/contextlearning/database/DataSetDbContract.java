/*******************************************************************************
 * Copyright (c) 2017 Maximilian Alexandru.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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

    public static class ActivityEntries implements BaseColumns {
        public static final String TABLE_NAME = "ActivityDataSet";
        public static final String COLUMN_NAME_TIME = "Time";
        public static final String COLUMN_NAME_LABEL = "Label";
    }

}
