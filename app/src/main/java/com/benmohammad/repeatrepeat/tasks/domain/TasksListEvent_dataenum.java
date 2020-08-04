package com.benmohammad.repeatrepeat.tasks.domain;

import com.benmohammad.repeatrepeat.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
public interface TasksListEvent_dataenum {

    dataenum_case RefreshRequested();
    dataenum_case NewTaskClicked();
    dataenum_case NavigateToTaskDetailsRequested(String taskId);
    dataenum_case TaskMarkedCompleted(String taskId);
    dataenum_case TaskMarkedActive(String taskId);
    dataenum_case ClearCompletedTaskRequested();
    dataenum_case FilterSelected(TasksFilterType filterType);
    dataenum_case TasksLoaded(ImmutableList<Task> tasks);
    dataenum_case TasksCreated();
    dataenum_case TasksRefreshed();
    dataenum_case TasksRefreshFailed();
    dataenum_case TasksLoadingFailed();
}
