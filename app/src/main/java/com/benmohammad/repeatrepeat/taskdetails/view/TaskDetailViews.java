package com.benmohammad.repeatrepeat.taskdetails.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.taskdetails.domain.TaskDetailEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class TaskDetailViews implements TaskDetailViewActions, Connectable<TaskDetailViewData, TaskDetailEvent> {

    private final FloatingActionButton fab;
    private final Observable<TaskDetailEvent> menuEvents;
    private TextView detailTitle;
    private TextView detailDescription;
    private CheckBox detailCompleteStatus;
    private View rootView;

    public TaskDetailViews(LayoutInflater inflater,
                           ViewGroup parent,
                           FloatingActionButton fab,
                           Observable<TaskDetailEvent> menuEvents) {
        rootView = inflater.inflate(R.layout.taskdetails_frag, parent, false);
        this.menuEvents = menuEvents;
        detailTitle = rootView.findViewById(R.id.task_detail_title);
        detailDescription = rootView.findViewById(R.id.task_detail_description);
        detailCompleteStatus = rootView.findViewById(R.id.task_detail_complete);
        this.fab = fab;

    }

    public View getRootView() {
        return rootView;
    }


    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(rootView, R.string.task_marked_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(rootView, R.string.task_marked_active, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskSavingFailed() {
        Snackbar.make(rootView, "Failed to Save", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskDeletionFailed() {
        Snackbar.make(rootView, "Failed to Delete", Snackbar.LENGTH_SHORT).show();
    }

    @Nonnull
    @Override
    public Connection<TaskDetailViewData> connect(Consumer<TaskDetailEvent> output) throws ConnectionLimitExceededException {
        fab.setOnClickListener(__ -> output.accept(TaskDetailEvent.editTaskRequested()));

        detailCompleteStatus.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if(isChecked) {
                        output.accept(TaskDetailEvent.completeTaskRequested());
                    } else {
                        output.accept(TaskDetailEvent.activateTaskRequested());
                    }
                }
        );

        Disposable disposable = menuEvents
                .retry()
                .subscribe(
                        output::accept,
                        t -> Log.e(TaskDetailViews.class.getSimpleName(), "Menu events fail"));

        return new Connection<TaskDetailViewData>() {
            @Override
            public void accept(TaskDetailViewData value) {
                render(value);
            }

            @Override
            public void dispose() {
                disposable.dispose();
                fab.setOnClickListener(null);
                detailCompleteStatus.setOnCheckedChangeListener(null);
            }
        };
    }

    private void render(TaskDetailViewData viewData) {
        detailCompleteStatus.setChecked(viewData.completedChecked());
        bindTextViewData(detailTitle, viewData.title());
        bindTextViewData(detailDescription, viewData.description());
    }

    private void bindTextViewData(TextView textView, TaskDetailViewData.TextViewData viewData) {
        textView.setVisibility(viewData.visibility());
        textView.setText(viewData.text());

    }}
