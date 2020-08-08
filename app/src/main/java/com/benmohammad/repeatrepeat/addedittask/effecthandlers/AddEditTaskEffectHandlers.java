package com.benmohammad.repeatrepeat.addedittask.effecthandlers;

import android.content.Context;

import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEffect;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEffect.CreateTask;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEffect.Exit;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEffect.NotifyEmptyTaskNotAllowed;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEffect.SaveTask;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEvent;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.source.TasksDataSource;
import com.benmohammad.repeatrepeat.data.source.local.TasksLocalDataSource;
import com.benmohammad.repeatrepeat.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEvent;
import com.benmohammad.repeatrepeat.util.schedulers.SchedulerProvider;
import com.spotify.mobius.rx2.RxMobius;

import java.util.UUID;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class AddEditTaskEffectHandlers {

    public static ObservableTransformer<AddEditTaskEffect, AddEditTaskEvent> createEffectHandlers(
            Context context, Action showTasksList, Action showEmptyTaskError) {
        TasksRemoteDataSource remoteDataSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<AddEditTaskEffect, AddEditTaskEvent>subtypeEffectHandler()
                .addAction(NotifyEmptyTaskNotAllowed.class, showEmptyTaskError, mainThread())
                .addAction(Exit.class, showTasksList, mainThread())
                .addFunction(CreateTask.class, createTaskHandler(remoteDataSource, localDataSource))
                .addFunction(SaveTask.class, saveTaskHandler(remoteDataSource, localDataSource))
                .build();
    }


    static Function<CreateTask, AddEditTaskEvent> createTaskHandler(
            TasksDataSource remoteDataSource, TasksDataSource localDataSource) {
        return createTaskEffect -> {
            Task task = Task.create(UUID.randomUUID().toString(), createTaskEffect.taskDetails());
            try {
                remoteDataSource.saveTask(task);
                localDataSource.saveTask(task);
                return AddEditTaskEvent.taskCreatedSuccessfully();
            } catch (Exception e) {
                return AddEditTaskEvent.taskCreationFailed("Failed to create Task");
            }
        };
    }

    static Function<SaveTask, AddEditTaskEvent> saveTaskHandler(
            TasksDataSource remoteDataSource, TasksDataSource localDatSource) {
        return saveTasks -> {
            try {
                remoteDataSource.saveTask(saveTasks.task());
                localDatSource.saveTask(saveTasks.task());
                return AddEditTaskEvent.taskUpdatedSuccessfully();
            } catch (Exception e) {
                return AddEditTaskEvent.taskCreationFailed("Failed to update Task");
            }
        };
    }
}
