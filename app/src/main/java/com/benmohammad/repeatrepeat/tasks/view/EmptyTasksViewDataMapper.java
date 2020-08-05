package com.benmohammad.repeatrepeat.tasks.view;

import android.view.View;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.tasks.domain.TasksFilterType;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewData.EmptyTasksViewData;

public class EmptyTasksViewDataMapper {

    public static EmptyTasksViewData createEmptyTaskViewData(TasksFilterType filterType) {
        EmptyTasksViewData.Builder builder = EmptyTasksViewData.builder();
        switch(filterType) {
            case ACTIVE_TASKS:
                return builder
                        .addViewVisibility(View.GONE)
                        .title(R.string.no_tasks_completed)
                        .icon(R.drawable.ic_verified)
                        .build();
            case COMPLETED_TASKS:
                return builder
                        .addViewVisibility(View.GONE)
                        .title(R.string.no_tasks_active)
                        .icon(R.drawable.ic_circle_check)
                        .build();
            default:
                return builder
                        .addViewVisibility(View.VISIBLE)
                        .title(R.string.no_tasks_all)
                        .icon(R.drawable.ic_assignment)
                        .build();
        }
    }
}
