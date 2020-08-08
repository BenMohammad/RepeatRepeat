package com.benmohammad.repeatrepeat.addedittask;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEvent;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskMode;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskModel;
import com.benmohammad.repeatrepeat.addedittask.view.AddEditTaskModeBundlePacker;
import com.benmohammad.repeatrepeat.addedittask.view.AddEditTaskViews;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskBundlePacker;
import com.benmohammad.repeatrepeat.data.TaskDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.mobius.MobiusLoop;

import static com.benmohammad.repeatrepeat.addedittask.AddEditTaskInjector.createController;
import static com.benmohammad.repeatrepeat.addedittask.effecthandlers.AddEditTaskEffectHandlers.createEffectHandlers;
import static com.benmohammad.repeatrepeat.addedittask.view.AddEditTaskModeBundlePacker.addEditTaskModelToBundle;
import static com.spotify.mobius.internal_util.Preconditions.checkNotNull;

public class AddEditTaskFragment extends Fragment {

    public static final String TASK_ARGUMENT = "task";
    public static final String ADD_EDIT_TASK_MODEL_RESTORE_KEY = "add_edit_task_model";

    private MobiusLoop.Controller<AddEditTaskModel, AddEditTaskEvent> controller;

    public static AddEditTaskFragment newInstanceForTaskCreation() {
        return new AddEditTaskFragment();
    }

    public static AddEditTaskFragment newInstanceForTaskUpdate(Task task) {
        AddEditTaskFragment fragment = new AddEditTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putBundle("task", TaskBundlePacker.taskToBundle(task));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task_done);
        AddEditTaskViews views = new AddEditTaskViews(inflater, container, fab);
        controller =
                createController(
                        createEffectHandlers(getContext(), this::finishWithResultOK, views::showEmptyTaskError),
                        resolveDefaultModel(savedInstanceState));

        controller.connect(views);
        setHasOptionsMenu(true);
        return views.getRootView();
    }

    private AddEditTaskModel resolveDefaultModel(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(TASK_ARGUMENT)){
            Task task = TaskBundlePacker.taskFromBundle(checkNotNull(arguments.getBundle(TASK_ARGUMENT)));
            return AddEditTaskModel.builder()
                    .details(task.details())
                    .mode(AddEditTaskMode.update(task.id()))
                    .build();
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(ADD_EDIT_TASK_MODEL_RESTORE_KEY)) {
            return AddEditTaskModeBundlePacker.addEdtTaskModelFromBundle(
                    checkNotNull(savedInstanceState.getBundle(ADD_EDIT_TASK_MODEL_RESTORE_KEY)));
        }

        return AddEditTaskModel.builder()
                .mode(AddEditTaskMode.create())
                .details(TaskDetails.DEFAULT)
                .build();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ADD_EDIT_TASK_MODEL_RESTORE_KEY, addEditTaskModelToBundle(controller.getModel()));
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
    public void onDestroyView() {
        controller.disconnect();
        super.onDestroyView();
    }

    private void finishWithResultOK() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
