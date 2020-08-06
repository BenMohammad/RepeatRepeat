package com.benmohammad.repeatrepeat.taskdetails.domain;

import androidx.annotation.NonNull;

import com.benmohammad.repeatrepeat.data.Task;
import com.spotify.mobius.Next;

import static com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEffect.*;
import static com.spotify.mobius.Effects.effects;

public class TaskDetailLogic {

    @NonNull
    public static Next<Task, TaskDetailEffect> update(Task task, TaskDetailEvent event) {
        return event.map(
                deleteTaskRequested -> Next.dispatch(effects(deleteTask(task))),
                completeTaskRequested -> onCompleteTaskRequested(task),
                activateTAskRequested -> onActivateTaskRequested(task),
                editTaskRequested -> Next.dispatch(effects(openTaskEditor(task))),
                taskDeleted -> Next.dispatch(effects(exit())),
                taskCompleted -> Next.dispatch(effects(notifyTaskMarkedComplete())),
                taskActivated -> Next.dispatch(effects(notifyTaskMarkedActive())),
                taskSaveFailed -> Next.noChange(),
                taskDeletionFailed -> Next.noChange());
    }

    private static Next<Task, TaskDetailEffect> onActivateTaskRequested(Task task) {
        if(!task.details().completed()) {
            return Next.noChange();
        }
        Task activateTask = task.activate();
        return Next.next(activateTask, effects(saveTask(activateTask)));
    }

    private static Next<Task, TaskDetailEffect> onCompleteTaskRequested(Task task) {
        if(task.details().completed()) {
            return Next.noChange();
        }
        Task completedTask = task.complete();
        return Next.next(completedTask, effects(saveTask(completedTask)));
    }
}
