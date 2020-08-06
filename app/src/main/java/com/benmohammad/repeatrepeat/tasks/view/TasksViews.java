package com.benmohammad.repeatrepeat.tasks.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewData.EmptyTasksViewData;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewData.TaskViewData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.navigateToTaskDetailsRequested;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.newTaskClicked;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.refreshRequested;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.taskMarkedActive;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.taskMarkedComplete;
import static com.benmohammad.repeatrepeat.tasks.domain.TasksListEvent.tasksCreated;

public class TasksViews implements TasksListViewActions, Connectable<TasksListViewData, TasksListEvent> {

    private final View root;
    private final ScrollChildSwipeRefreshLayout swipeRefreshLayout;
    private final FloatingActionButton fab;
    private final Observable<TasksListEvent> menuEvents;

    private TasksAdapter adapter;
    private View noTaskView;
    private ImageView noTaskIcon;
    private TextView noTaskMainView;
    private TextView noTaskAddView;
    private LinearLayout tasksView;

    private TextView filteringLabelView;

    public TasksViews(
            LayoutInflater inflater,
            ViewGroup parent,
            FloatingActionButton fab,
            Observable<TasksListEvent> menuEvents) {
        this.menuEvents = menuEvents;
        root = inflater.inflate(R.layout.tasks_frag, parent, false);
        adapter = new TasksAdapter();
        ListView listView = root.findViewById(R.id.tasks_list);
        listView.setAdapter(adapter);
        filteringLabelView = root.findViewById(R.id.filteringLabel);
        tasksView = root.findViewById(R.id.tasksLL);

        noTaskView = root.findViewById(R.id.noTasks);
        noTaskIcon = root.findViewById(R.id.noTasksIcon);
        noTaskMainView = root.findViewById(R.id.noTasksMain);
        noTaskAddView = root.findViewById(R.id.noTasksAdd);
        fab.setImageResource(R.drawable.ic_add);
        this.fab = fab;
        swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(root.getContext(), R.color.colorAccent),
                ContextCompat.getColor(root.getContext(), R.color.colorPrimary),
                ContextCompat.getColor(root.getContext(), R.color.colorPrimaryDark)
        );

        swipeRefreshLayout.setScrollUpChild(listView);
    }

    public View getRootView() {
        return this.root;
    }

    private void showMessage(int messageRes) {
        Snackbar.make(root, messageRes, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage(R.string.task_marked_complete);
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(R.string.task_marked_active);
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage(R.string.completed_tasks_cleared);
    }

    @Override
    public void showLoadingTaskError() {
        showMessage(R.string.loading_tasks_error);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(R.string.successfully_saved_task_message);
    }

    @Nonnull
    @Override
    public Connection<TasksListViewData> connect(Consumer<TasksListEvent> output) throws ConnectionLimitExceededException {
        addUiListener(output);
        Disposable disposable = menuEvents.subscribe(output::accept);

        return new Connection<TasksListViewData>() {
            @Override
            public void accept(TasksListViewData value) {
                render(value);
            }

            @Override
            public void dispose() {
                disposable.dispose();
                noTaskAddView.setOnClickListener(null);
                fab.setOnClickListener(null);
                swipeRefreshLayout.setOnRefreshListener(null);
                adapter.setTaskItemListener(null);
            }
        };
    }

    private void addUiListener(Consumer<TasksListEvent> output) {
        noTaskAddView.setOnClickListener(__ -> output.accept(newTaskClicked()));
        fab.setOnClickListener(__ -> output.accept(newTaskClicked()));
        swipeRefreshLayout.setOnRefreshListener(() -> output.accept(refreshRequested()));
        adapter.setTaskItemListener(new TasksAdapter.TaskItemListener() {
            @Override
            public void onTaskClick(String id) {
                output.accept(navigateToTaskDetailsRequested(id));
            }

            @Override
            public void onCompleteTaskClick(String id) {
                output.accept(taskMarkedComplete(id));
            }

            @Override
            public void onActivateTaskClick(String id) {
                output.accept(taskMarkedActive(id));
            }
        });
    }

    private void showEmptyTaskState(EmptyTasksViewData vd) {
        tasksView.setVisibility(View.GONE);
        noTaskView.setVisibility(View.VISIBLE);
        noTaskMainView.setText(vd.title());
        noTaskIcon.setVisibility(vd.addViewVisibility());
    }


    private void showNoTasksViewState() {
        tasksView.setVisibility(View.GONE);
        noTaskView.setVisibility(View.GONE);
    }

    private void showTasks(ImmutableList<TaskViewData> tasks) {
        adapter.replaceData(tasks);
        tasksView.setVisibility(View.VISIBLE);
        noTaskView.setVisibility(View.GONE);
    }

    private void render(TasksListViewData value) {
        swipeRefreshLayout.setRefreshing(value.loading());
        filteringLabelView.setText(value.filterLabel());
        value
                .viewState()
                .match(
                        awaitingTasks -> showNoTasksViewState(),
                        emptyTasks -> showEmptyTaskState(emptyTasks.viewData()),
                        hasTasks -> showTasks(hasTasks.taskViewData())
                );
    }
}
