package com.benmohammad.repeatrepeat.data.source.remote;

import androidx.annotation.NonNull;

import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskDetails;
import com.benmohammad.repeatrepeat.data.source.TasksDataSource;
import com.google.common.base.Optional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;
    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;
    private static final Map<String, Task> TASK_SERVICE_DATA;

    static {
        TASK_SERVICE_DATA = new LinkedHashMap<>();
        addTask("1234", "Build Better Apps", "Hacking away");
        addTask("5678", "Learn eveerything", "HAck Hack Hack");
    }


    public static TasksRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    private static void addTask(String id, String title, String description) {
        Task newTask = Task.create(id, TaskDetails.create(title, description, false));
        TASK_SERVICE_DATA.put(newTask.id(), newTask);
    }

    @Override
    public Flowable<List<Task>> getTasks() {
        return Flowable.fromIterable(TASK_SERVICE_DATA.values())
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList()
                .toFlowable();
    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        final Task task = TASK_SERVICE_DATA.get(taskId);
        if(task != null) {
            return Flowable.just(Optional.of(task))
                    .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS);
        } else {
            return Flowable.empty();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASK_SERVICE_DATA.put(task.id(), task);
    }

    @Override
    public void deleteAllTasks() {
        TASK_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASK_SERVICE_DATA.remove(taskId);
    }
}
