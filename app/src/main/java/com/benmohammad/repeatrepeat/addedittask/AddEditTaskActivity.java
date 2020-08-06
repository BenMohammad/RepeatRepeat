package com.benmohammad.repeatrepeat.addedittask;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    public static Intent addTask(Context context) {
        return new Intent(context, AddEditTaskActivity.class);
    }
}
