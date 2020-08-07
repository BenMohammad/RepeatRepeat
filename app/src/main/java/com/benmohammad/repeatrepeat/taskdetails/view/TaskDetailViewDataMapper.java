package com.benmohammad.repeatrepeat.taskdetails.view;

import android.view.View;

import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskDetails;

import static com.google.common.base.Strings.isNullOrEmpty;

public class TaskDetailViewDataMapper {

    public static TaskDetailViewData taskToTaskViewData(Task task) {
        TaskDetails details = task.details();
        String title = details.title();
        String description = details.description();

        return TaskDetailViewData.builder()
                .title(TaskDetailViewData.TextViewData.create(isNullOrEmpty(title) ? View.GONE : View.VISIBLE, title))
                .description(TaskDetailViewData.TextViewData.create(isNullOrEmpty(description)  ? View.GONE : View.VISIBLE, description))
                .completedChecked(details.completed())
                .build();
    }
}
