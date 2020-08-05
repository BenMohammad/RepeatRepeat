package com.benmohammad.repeatrepeat.tasks.view;

public interface TasksListViewActions {

    void showTaskMarkedComplete();
    void showTaskMarkedActive();
    void showCompletedTasksCleared();
    void showLoadingTaskError();
    void showSuccessfullySavedMessage();
}
