package com.benmohammad.repeatrepeat.stats;

import com.benmohammad.repeatrepeat.stats.domain.StatisticsEffect;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsEvent;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsLogic;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsState;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.android.MobiusAndroid;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;

public class StatisticsInjector {

    public static MobiusLoop.Controller<StatisticsState, StatisticsEvent> createController(
            ObservableTransformer<StatisticsEffect, StatisticsEvent> effectHandler,
            StatisticsState defaultState){
        return MobiusAndroid.controller(createLoop(effectHandler), defaultState);

    }

    private static MobiusLoop.Factory<StatisticsState, StatisticsEvent, StatisticsEffect> createLoop(
            ObservableTransformer<StatisticsEffect, StatisticsEvent> effectHandler) {
        return RxMobius.loop(StatisticsLogic::update, effectHandler).init(StatisticsLogic::init);
    }
}
