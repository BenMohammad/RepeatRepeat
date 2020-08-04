package com.benmohammad.repeatrepeat.tasks.domain;

import android.graphics.Path;

import androidx.annotation.Nullable;

import com.benmohammad.repeatrepeat.data.Task;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import static com.spotify.dataenum.DataenumUtils.checkNotNull;

@AutoValue
public abstract class TasksListModel {


    @Nullable
    public abstract ImmutableList<Task> tasks();
    public abstract TasksFilterType filter();
    public abstract boolean loading();

    public int findTaskIndexById(String id) {
        ImmutableList<Task> tasks = checkNotNull(tasks());
        int taskIndex = -1;
        for(int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).id().equals(id)) {
                taskIndex = i;
                break;
            }
        }
        return taskIndex;
    }

    public Optional<Task> findTaskById(String id) {
        int taskIndex = findTaskIndexById(id);
        if(taskIndex < 0) return Optional.absent();
        return Optional.of(checkNotNull(tasks().get(taskIndex)));
    }

    public TasksListModel withTasks(ImmutableList<Task> tasks) {
        return toBuilder().tasks(tasks).build();
    }

    public TasksListModel withLoading(boolean loading) {
        return toBuilder().loading(loading).build();
    }

    public TasksListModel withTasksFilter(TasksFilterType tasksFilter) {
        return toBuilder().filter(tasksFilter).build();
    }

    public TasksListModel withTaskAtIndex(Task task, int index) {
        ImmutableList<Task> tasks = checkNotNull(tasks());
        assertIndexWithinBounds(index, tasks);

        ArrayList<Task> copy = new ArrayList<>(tasks);
        copy.set(index, task);
        return withTasks(ImmutableList.copyOf(copy));
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_TasksListModel.Builder().loading(false).filter(TasksFilterType.ALL_TASKS);
    }

    static void assertIndexWithinBounds(int index, List<?> items) {
        if(index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("index out of bounds");
        }
    }

    @AutoValue
    public abstract static class TaskEntry {
        public abstract int index();
        public abstract Task task();

        public static TaskEntry create(int index, Task task) {
            return new AutoValue_TasksListModel_TaskEntry(index, task);
        }


    }
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder tasks(ImmutableList<Task> tasks);
        public abstract Builder filter(TasksFilterType filter);
        public abstract Builder loading(boolean loading);
        public abstract TasksListModel build();
    }
}
