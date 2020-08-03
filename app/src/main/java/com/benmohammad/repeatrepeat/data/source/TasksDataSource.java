package com.benmohammad.repeatrepeat.data.source;

import androidx.annotation.NonNull;

import com.benmohammad.repeatrepeat.data.Task;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Flowable;

public interface TasksDataSource {

    Flowable<List<Task>> getTasks();

    Flowable<Optional<Task>> getTask(@NonNull String taskId);
    void saveTask(@NonNull Task task);
    void deleteAllTasks();
    void deleteTask(@NonNull String taskId);
}
