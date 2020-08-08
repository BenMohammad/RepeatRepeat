package com.benmohammad.repeatrepeat.addedittask.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEvent;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskModel;
import com.benmohammad.repeatrepeat.taskdetails.view.TaskDetailViewData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

public class AddEditTaskViews implements Connectable<AddEditTaskModel, AddEditTaskEvent> {

    private final View root;
    private final FloatingActionButton fab;
    private final TextView title;
    private final TextView description;

    public AddEditTaskViews(LayoutInflater inflater, ViewGroup parent, FloatingActionButton fab) {
        root = inflater.inflate(R.layout.addtask_frag, parent, false);
        title = root.findViewById(R.id.add_task_title);
        description = root.findViewById(R.id.add_task_description);
        fab.setImageResource(R.drawable.ic_done);
        this.fab = fab;
    }

    public View getRootView() {
        return root;
    }

    public void showEmptyTaskError() {
        Snackbar.make(title, R.string.empty_task_message, Snackbar.LENGTH_SHORT).show();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    @Nonnull
    @Override
    public Connection<AddEditTaskModel> connect(Consumer<AddEditTaskEvent> output) throws ConnectionLimitExceededException {
        fab.setOnClickListener(
                __ ->
                        output.accept(
                                AddEditTaskEvent.taskDefinitionCompleted(
                                        title.getText().toString(), description.getText().toString())));

        return new Connection<AddEditTaskModel>() {
            @Override
            public void accept(AddEditTaskModel model) {
                setTitle(model.details().title());
                setDescription(model.details().description());
            }

            @Override
            public void dispose() {
                fab.setOnClickListener(null);
            }
        };
    }
}
