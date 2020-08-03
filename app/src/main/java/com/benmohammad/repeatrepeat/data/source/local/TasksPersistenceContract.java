package com.benmohammad.repeatrepeat.data.source.local;

import android.provider.BaseColumns;

public final class TasksPersistenceContract {

    private TasksPersistenceContract(){}

    public abstract static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "allTasks";
        public static final String COLUMN_NAME_ENTRY_ID = "entryId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}
