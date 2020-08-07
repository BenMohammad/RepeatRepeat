package com.benmohammad.repeatrepeat.taskdetails.effecthandlers;

import android.content.Context;

import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.source.TasksDataSource;
import com.benmohammad.repeatrepeat.data.source.local.TasksLocalDataSource;
import com.benmohammad.repeatrepeat.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.DeleteTask;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.NotifyTaskDeletionFailed;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.NotifyTaskMarkedActive;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.NotifyTaskMarkedComplete;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.NotifyTaskSaveFailed;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.OpenTaskEditor;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.SaveTask;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEvent;
import com.benmohammad.repeatrepeat.taskdetails.view.TaskDetailViewActions;
import com.benmohammad.repeatrepeat.taskdetails.view.TaskDetailViews;
import com.benmohammad.repeatrepeat.util.schedulers.SchedulerProvider;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TaskDetailEffectHandlers {


    public static ObservableTransformer<TaskDetailEffect, TaskDetailEvent> createEffectHandlers(
            TaskDetailViewActions view, Context context, Action dismiss, Consumer<Task> launchEditor) {
        TasksRemoteDataSource remoteDataSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<TaskDetailEffect, TaskDetailEvent>subtypeEffectHandler()
                .addFunction(DeleteTask.class, deleteTaskHandler(remoteDataSource, localDataSource))
                .addFunction(SaveTask.class, saveTaskHandler(remoteDataSource, localDataSource))
                .addAction(NotifyTaskMarkedComplete.class, view::showTaskMarkedComplete, mainThread())
                .addAction(NotifyTaskMarkedActive.class, view::showTaskMarkedActive, mainThread())
                .addAction(NotifyTaskDeletionFailed.class, view::showTaskDeletionFailed, mainThread())
                .addAction(NotifyTaskSaveFailed.class, view::showTaskSavingFailed, mainThread())
                .addConsumer(OpenTaskEditor.class, openTaskEditorHandler(launchEditor), mainThread())
                .addAction(TaskDetailEffect.Exit.class, dismiss, mainThread())
                .build();
    }



    private static Consumer<OpenTaskEditor> openTaskEditorHandler(Consumer<Task> launchEditorCommand) {
        return openTaskEditor -> launchEditorCommand.accept(openTaskEditor.task());
    }

    private static Function<SaveTask, TaskDetailEvent> saveTaskHandler(
        TasksDataSource remoteSource, TasksDataSource localSource) {
        return saveTask -> {
            try {
                remoteSource.saveTask(saveTask.task());
                localSource.saveTask(saveTask.task());
                return saveTask.task().details().completed() ? TaskDetailEvent.taskMarkedComplete() : TaskDetailEvent.taskMarkedActive();
            } catch(Exception e) {
                return TaskDetailEvent.taskSaveFailed();
            }
        };
    }

    private static Function<DeleteTask, TaskDetailEvent> deleteTaskHandler(
            TasksDataSource remoteSource, TasksDataSource localSource) {
        return deleteTask -> {
            try {
                remoteSource.deleteTask(deleteTask.task().id());
                localSource.deleteTask(deleteTask.task().id());
                return TaskDetailEvent.taskDeleted();
            } catch(Exception e) {
                return TaskDetailEvent.taskDeletionFailed();
            }
        };
    }
}
