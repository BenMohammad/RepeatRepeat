package com.benmohammad.repeatrepeat.addedittask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskBundlePacker;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    public static Intent editTask(Context c, Task task) {
        Intent i = new Intent(c, AddEditTaskActivity.class);
        i.putExtra("task to edit", TaskBundlePacker.taskToBundle(task));
        return i;
    }

    public static Intent addTask(Context context) {
        return new Intent(context, AddEditTaskActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);
    }
}
