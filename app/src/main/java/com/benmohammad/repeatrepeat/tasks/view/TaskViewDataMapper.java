package com.benmohammad.repeatrepeat.tasks.view;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskDetails;
import com.google.common.base.Strings;

public class TaskViewDataMapper {

    public static TasksListViewData.TaskViewData createTaskViewData(Task task) {
        if(task == null) return null;
        return TasksListViewData.TaskViewData.create(
                getTitleForLIst(task.details()),
                task.details().completed(),
                task.details().completed() ? R.drawable.list_completed_touch_feedback : R.drawable.touch_feedback,
                task.id()
        );
    }

    private static String getTitleForLIst(TaskDetails details) {
        if(!Strings.isNullOrEmpty(details.title())) {
            return details.title();
        } else {
            return details.description();
        }
    }
}
