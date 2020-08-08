package com.benmohammad.repeatrepeat.addedittask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskBundlePacker;
import com.benmohammad.repeatrepeat.util.ActivityUtils;

import static com.spotify.mobius.internal_util.Preconditions.checkNotNull;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;
    private ActionBar actionBar;

    public static Intent editTask(Context c, Task task) {
        Intent i = new Intent(c, AddEditTaskActivity.class);
        i.putExtra("task_to_edit", TaskBundlePacker.taskToBundle(task));
        return i;
    }

    public static Intent addTask(Context context) {
        return new Intent(context, AddEditTaskActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = checkNotNull(getSupportActionBar());
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        Task task;
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("task_to_edit")) {
            Bundle bundledTask = checkNotNull(extras.getBundle("task_to_edit"));
            task = TaskBundlePacker.taskFromBundle(bundledTask);
            setToolbarTitle(task.id());
        } else {
            task = null;
            setToolbarTitle(null);
        }

        if(addEditTaskFragment == null) {
            addEditTaskFragment = task == null ? AddEditTaskFragment.newInstanceForTaskCreation() :
                    AddEditTaskFragment.newInstanceForTaskUpdate(task);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addEditTaskFragment, R.id.contentFrame);
        }
    }

    private void setToolbarTitle(@Nullable String taskId) {
        if(taskId == null) {
            actionBar.setTitle(R.string.add_task);
        }else {
            actionBar.setTitle(R.string.edit_task);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
