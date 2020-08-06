package com.benmohammad.repeatrepeat.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.addedittask.AddEditTaskActivity;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.taskdetails.TaskDetailActivity;
import com.benmohammad.repeatrepeat.tasks.domain.TaskFilters;
import com.benmohammad.repeatrepeat.tasks.domain.TasksFilterType;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListModel;
import com.benmohammad.repeatrepeat.tasks.view.DeferredEventSource;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewData;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewDataMapper;
import com.benmohammad.repeatrepeat.tasks.view.TasksViews;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.mobius.MobiusLoop;

import io.reactivex.subjects.PublishSubject;

import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.clearCompletedTaskRequested;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.filterSelected;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.refreshRequested;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListModelBundlePacker.tasksListModelFromBundle;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListModelBundlePacker.tasksListModelToBundle;
import static com.benmohammad.repeatrepeat.tasks.effecthandlers.TasksListEffectHandlers.createEffectHandler;
import static com.spotify.mobius.extras.Connectables.contramap;

public class TasksFragment extends Fragment {

    private MobiusLoop.Controller<TasksListModel, TasksListEvent> controller;
    private PublishSubject<TasksListEvent> menuEvents = PublishSubject.create();
    private TasksViews views;
    private DeferredEventSource<TasksListEvent> eventSource = new DeferredEventSource<>();

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_task);
        views = new TasksViews(inflater, container, fab, menuEvents);

        controller = TasksInjector.createController(
                createEffectHandler(
                        getContext(), views, this::showAddTask, this::showTaskDetailUi),
                eventSource,
                resolveDefaultModel(savedInstanceState));

        controller.connect(contramap(TasksListViewDataMapper::tasksListModelToViewData, views));
        setHasOptionsMenu(true);
        return views.getRootView();
    }

    private TasksListModel resolveDefaultModel(@Nullable Bundle savedInstanceState) {
        return savedInstanceState != null ? tasksListModelFromBundle(savedInstanceState.getBundle("model")) :TasksListModel.DEFAULT;
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", tasksListModelToBundle(controller.getModel()));
    }

    @Override
    public void onDestroy() {
        controller.disconnect();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_clear:
                menuEvents.onNext(clearCompletedTaskRequested());
                break;
            case R.id.menu_filter:
                showFilteringPopupMenu();
                break;
            case R.id.menu_refresh:
                menuEvents.onNext(refreshRequested());
                break;
        }

        return true;
    }

    private void onFilterSelected(TasksFilterType filter) {
        menuEvents.onNext(filterSelected(filter));
    }

    private void showFilteringPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popupMenu.getMenuInflater().inflate(R.menu.filter_tasks, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(
                item -> {
                    switch(item.getItemId()) {
                        case R.id.active:
                            onFilterSelected(TasksFilterType.ACTIVE_TASKS);
                            break;
                        case R.id.completed:
                            onFilterSelected(TasksFilterType.COMPLETED_TASKS);
                            break;
                        default:
                            onFilterSelected(TasksFilterType.ALL_TASKS);
                            break;
                    }
                    return true;
                }
        );
        popupMenu.show();
    }

    public void showAddTask() {
        startActivityForResult(AddEditTaskActivity.addTask(getContext()), AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    public void showTaskDetailUi(Task task) {
        startActivity(TaskDetailActivity.showTask(getContext(), task));
    }
}
