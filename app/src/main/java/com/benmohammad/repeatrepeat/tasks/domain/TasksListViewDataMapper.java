package com.benmohammad.repeatrepeat.tasks.domain;

import android.icu.text.AlphabeticIndex;

import androidx.annotation.Nullable;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.tasks.view.TaskViewDataMapper;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewData;
import com.benmohammad.repeatrepeat.tasks.view.ViewState;
import com.google.common.collect.ImmutableList;

import static com.benmohammad.repeatrepeat.tasks.view.EmptyTasksViewDataMapper.createEmptyTaskViewData;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;

public class TasksListViewDataMapper {

    public static TasksListViewData taskListModelToViewData(TasksListModel model) {
        return TasksListViewData.builder()
                .loading(model.loading())
                .filterLabel(getFilterLabel(model.filter()))
                .viewState(getViewState(model.tasks(), model.filter()))
                .build();
    }

    private static ViewState getViewState(@Nullable ImmutableList<Task> tasks, TasksFilterType filter) {
        if(tasks == null) return ViewState.awaitingTasks();
        ImmutableList<Task> filteredTasks = TaskFilters.filterTasks(tasks, filter);
        if(filteredTasks.isEmpty()) {
            return ViewState.emptyTasks(createEmptyTaskViewData(filter));
        } else {
            return ViewState.hasTasks(copyOf(transform(filteredTasks, TaskViewDataMapper::createTaskViewData)));
        }
    }

    private static int getFilterLabel(TasksFilterType filterType) {
        switch (filterType) {
            case ACTIVE_TASKS:
                return R.string.label_active;
            case COMPLETED_TASKS:
                return R.string.label_completed;
            default:
                return R.string.label_all;
        }
    }}
