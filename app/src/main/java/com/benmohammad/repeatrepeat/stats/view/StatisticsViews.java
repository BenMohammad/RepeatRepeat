package com.benmohammad.repeatrepeat.stats.view;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsEvent;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsState;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

public class StatisticsViews implements Connectable<StatisticsState, StatisticsEvent> {

    private final View root;
    private final TextView statisticsTV;

    public StatisticsViews(LayoutInflater inflater, ViewGroup parent) {
        root = inflater.inflate(R.layout.statistics_frag, parent, false);
        statisticsTV = root.findViewById(R.id.statistics);
    }

    public View getRootView() {
        return root;
    }

    @Nonnull
    @Override
    public Connection<StatisticsState> connect(Consumer<StatisticsEvent> output) throws ConnectionLimitExceededException {
        return new Connection<StatisticsState>() {
            @Override
            public void accept(StatisticsState value) {
                renderState(value);
            }

            @Override
            public void dispose() {

            }
        };
    }

    private void renderState(StatisticsState state) {
        state.match(
                loading -> statisticsTV.setText(R.string.loading),
                loaded -> {
                    int numberOfCompletedTasks = loaded.completedCount();
                    int numberOfIncompleteTasks = loaded.activeCount();
                    if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
                        statisticsTV.setText(R.string.statistics_no_tasks);
                    } else {
                        Resources resources = root.getContext().getResources();
                        String activeTasksString =
                                "Number of ActiveTasks " + numberOfIncompleteTasks;
                        String completedTasksString =
                                "Number of CompletedTasks "+ numberOfCompletedTasks;
                        String displayString = activeTasksString + "\n" + completedTasksString;
                        statisticsTV.setText(displayString);
                    }
                },
                failed -> statisticsTV.setText(R.string.statistics_error));
    }
}
