package com.benmohammad.repeatrepeat.tasks.effecthandlers;

import android.content.Context;

import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.source.TasksDataSource;
import com.benmohammad.repeatrepeat.data.source.local.TasksLocalDataSource;
import com.benmohammad.repeatrepeat.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.DeleteTasks;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.LoadTasks;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.NavigateToTaskDetails;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.RefreshTasks;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.SaveTask;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.ShowFeedback;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEffect.StartTaskCreationFlow;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewActions;
import com.benmohammad.repeatrepeat.util.Either;
import com.benmohammad.repeatrepeat.util.schedulers.SchedulerProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.io.InsecureRecursiveDeleteException;
import com.spotify.mobius.rx2.RxMobius;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TasksListEffectHandlers {


    public static ObservableTransformer<TasksListEffect, TasksListEvent> createEffectHandler(
            Context context,
            TasksListViewActions view,
            Action showAddTask,
            Consumer<Task> showTaskDetails
    ) {
        TasksRemoteDataSource remoteDataSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<TasksListEffect, TasksListEvent>subtypeEffectHandler()
                .addTransformer(RefreshTasks.class, refreshTaskHandler(remoteDataSource, localDataSource))
                .addTransformer(LoadTasks.class, loadTaskHandler(localDataSource))
                .addConsumer(SaveTask.class, saveTaskHandler(remoteDataSource, localDataSource))
                .addConsumer(DeleteTasks.class, deleteTaskHandler(remoteDataSource, localDataSource))
                .addConsumer(ShowFeedback.class, showFeedbackHandler(view), mainThread())
                .addConsumer(NavigateToTaskDetails.class, navigateToDetailsHandler(showTaskDetails), mainThread())
                .addAction(StartTaskCreationFlow.class, showAddTask, mainThread())
                .build();
    }

    static ObservableTransformer<RefreshTasks, TasksListEvent> refreshTaskHandler(
            TasksDataSource remoteDataSource, TasksDataSource localDataSource
    ) {
        Single<TasksListEvent> refreshTaskOperation =
                remoteDataSource
                .getTasks()
                .singleOrError()
                .map(Either::<Throwable, List<Task>> right)
                .flatMap(
                        either -> either.map(
                                left -> Single.just(TasksListEvent.tasksLoadingFailed()),
                                right -> Observable.fromIterable(right.value())
                                .concatMapCompletable(
                                        t -> Completable.fromAction(() -> localDataSource.saveTask(t)))
                                        .andThen(Single.just(TasksListEvent.tasksRefreshed()))
                                        .onErrorReturnItem(TasksListEvent.tasksLoadingFailed())));

                return refreshTasks -> refreshTasks.flatMapSingle(__ -> refreshTaskOperation);
    }

    static ObservableTransformer<LoadTasks, TasksListEvent> loadTaskHandler(
            TasksDataSource dataSource
    ) {
        return loadTasks ->
                loadTasks.flatMap(
                        effect -> dataSource
                        .getTasks()
                        .toObservable()
                        .take(1)
                        .map(tasks -> TasksListEvent.tasksLoaded(ImmutableList.copyOf(tasks)))
                        .onErrorReturnItem(TasksListEvent.tasksLoadingFailed()));
    }

    static Consumer<SaveTask> saveTaskHandler(
            TasksDataSource remoteSource, TasksDataSource localDataSource
    ) {
        return saveTasks -> {
            remoteSource.saveTask(saveTasks.task());
            localDataSource.saveTask(saveTasks.task());
        };
    }

    static Consumer<DeleteTasks> deleteTaskHandler(
            TasksDataSource remoteDataSource, TasksDataSource localDataSource
    ) {
        return deleteTasks -> {
            for(Task task : deleteTasks.tasks()) {
                remoteDataSource.deleteTask(task.id());
                localDataSource.deleteTask(task.id());
            }
        };
    }

    static Consumer<ShowFeedback> showFeedbackHandler(TasksListViewActions view) {
        return showFeedback -> {
            switch(showFeedback.feedbackType()) {
                case SAVED_SUCCESSFULLY:
                    view.showSuccessfullySavedMessage();
                    break;
                case MARKED_ACTIVE:
                    view.showTaskMarkedActive();
                    break;
                case MARKED_COMPLETE:
                    view.showTaskMarkedComplete();
                    break;
                case CLEARED_COMPLETED:
                    view.showCompletedTasksCleared();
                case LOADING_ERROR:
                    view.showLoadingTaskError();
                    break;
            }
        };
    }

    static Consumer<NavigateToTaskDetails> navigateToDetailsHandler(Consumer<Task> command) {
        return navigateEffect -> command.accept(navigateEffect.task());
    }
}
