package com.benmohammad.repeatrepeat.taskdetails;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskBundlePacker;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    public static Intent showTask(Context context, Task task) {
        Intent i = new Intent(context, TaskDetailActivity.class);
        i.putExtra(EXTRA_TASK_ID, TaskBundlePacker.taskToBundle(task));
        return i;
    }
}
