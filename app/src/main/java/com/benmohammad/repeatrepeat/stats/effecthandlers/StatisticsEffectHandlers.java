package com.benmohammad.repeatrepeat.stats.effecthandlers;

import android.content.Context;

import com.benmohammad.repeatrepeat.data.source.local.TasksLocalDataSource;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsEffect;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsEvent;
import com.benmohammad.repeatrepeat.util.schedulers.SchedulerProvider;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;

public class StatisticsEffectHandlers {

    public static ObservableTransformer<StatisticsEffect, StatisticsEvent> createEffectHandler(
            Context context) {
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());
        return RxMobius.<StatisticsEffect, StatisticsEvent>subtypeEffectHandler()
                .addTransformer(StatisticsEffect.LoadTasks.class, loadTaskHandler(localDataSource))
                .build();

    }

    private static ObservableTransformer<StatisticsEffect.LoadTasks, StatisticsEvent> loadTaskHandler(
            TasksLocalDataSource localDataSource) {
        return effects -> effects.flatMap(
                loadTasks -> localDataSource
                .getTasks()
                .toObservable()
                .take(1)
                .map(ImmutableList::copyOf)
                .map(StatisticsEvent::tasksLoaded)
                .onErrorReturnItem(StatisticsEvent.tasksLoadingFailed())
        );
    }
}
