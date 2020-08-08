package com.benmohammad.repeatrepeat.stats.domain;

import androidx.annotation.NonNull;

import com.benmohammad.repeatrepeat.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.First;
import com.spotify.mobius.Next;

import static com.spotify.mobius.Effects.effects;

public final class StatisticsLogic {

    private StatisticsLogic(){}

    @NonNull
    public static First<StatisticsState, StatisticsEffect> init(StatisticsState state) {
        return state.map(
                loading -> First.first(state, effects(StatisticsEffect.loadTasks())),
                First::first,
                failed -> First.first(StatisticsState.loading(), effects(StatisticsEffect.loadTasks()))
        );

    }

    @NonNull
    public static Next<StatisticsState, StatisticsEffect> update(
            StatisticsState state, StatisticsEvent event) {
        return event.map(
                tasksLoaded -> {
                    ImmutableList<Task> tasks = tasksLoaded.tasks();
                    int activeCount = 0;
                    int completedCount= 0;
                    for(Task task: tasks) {
                        if(task.details().completed()) completedCount++;
                        else activeCount++;
                    }
                    return Next.next(StatisticsState.loaded(activeCount, completedCount));
                },
                tasksLoadingFailed -> Next.next(StatisticsState.failed())
        );

    }}
