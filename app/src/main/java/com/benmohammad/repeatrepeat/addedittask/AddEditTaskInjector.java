package com.benmohammad.repeatrepeat.addedittask;

import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEffect;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEvent;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskLogic;
import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskModel;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.android.AndroidLogger;
import com.spotify.mobius.android.MobiusAndroid;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;

public class AddEditTaskInjector {

    private AddEditTaskInjector(){}

    public static MobiusLoop.Controller<AddEditTaskModel, AddEditTaskEvent> createController(
            ObservableTransformer<AddEditTaskEffect, AddEditTaskEvent> effectHandlers,
            AddEditTaskModel defaultModel) {
        return MobiusAndroid.controller(createLoop(effectHandlers), defaultModel);
    }

    private static MobiusLoop.Factory<AddEditTaskModel, AddEditTaskEvent, AddEditTaskEffect> createLoop(
            ObservableTransformer<AddEditTaskEffect, AddEditTaskEvent> effectHandlers) {
        return RxMobius.loop(AddEditTaskLogic::update, effectHandlers)
                .logger(AndroidLogger.tag("Add/Edit Tasks"));
    }
}
