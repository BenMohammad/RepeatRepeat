package com.benmohammad.repeatrepeat.taskdetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.addedittask.AddEditTaskActivity;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskBundlePacker;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEvent;
import com.benmohammad.repeatrepeat.taskdetails.effecthandlers.TaskDetailEffectHandlers;
import com.benmohammad.repeatrepeat.taskdetails.view.TaskDetailViewDataMapper;
import com.benmohammad.repeatrepeat.taskdetails.view.TaskDetailViews;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.mobius.MobiusLoop;

import io.reactivex.subjects.PublishSubject;

import static com.spotify.mobius.extras.Connectables.contramap;

public class TaskDetailFragment extends Fragment {

    @NonNull private static final String ARGUMENT_TASK = "TASK";
    @NonNull private static final int REQUEST_EDIT_TASK = 1;

    private MobiusLoop.Controller<Task, TaskDetailEvent> controller;
    private TaskDetailViews taskDetailViews;
    private PublishSubject<TaskDetailEvent> menuEvents = PublishSubject.create();

    public static TaskDetailFragment newInstance(Task task) {
        Bundle arguments = new Bundle();
        arguments.putBundle(ARGUMENT_TASK, TaskBundlePacker.taskToBundle(task));
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task);
        taskDetailViews = new TaskDetailViews(inflater, container, fab, menuEvents);
        controller =
                TaskDetailInjector.createController(
                        TaskDetailEffectHandlers.createEffectHandlers(
                                taskDetailViews, getContext(), this::dismiss, this::openTaskEditor),
                        resolveDefaultModel(savedInstanceState));

        controller.connect(contramap(TaskDetailViewDataMapper::taskToTaskViewData, taskDetailViews));
        return taskDetailViews.getRootView();
    }

    private Task resolveDefaultModel(Bundle savedInstanceState) {
        Task t;
        if(savedInstanceState != null && savedInstanceState.containsKey(ARGUMENT_TASK)) {
            t = TaskBundlePacker.taskFromBundle(savedInstanceState.getBundle(ARGUMENT_TASK));
        } else {
            t = TaskBundlePacker.taskFromBundle(getArguments().getBundle(ARGUMENT_TASK));
        }
        return t;
    }

    @Override
    public void onDestroy() {
        controller.disconnect();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ARGUMENT_TASK, TaskBundlePacker.taskToBundle(controller.getModel()));
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.start();
    }

    @Override
    public void onPause() {
        controller.stop();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete:
                menuEvents.onNext(TaskDetailEvent.deleteTaskRequested());
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void openTaskEditor(@NonNull Task task) {
        startActivityForResult(AddEditTaskActivity.editTask(getContext(), task), REQUEST_EDIT_TASK);
    }

    private void dismiss() {
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_EDIT_TASK) {
            if(resultCode == Activity.RESULT_OK) {
                getActivity().finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
