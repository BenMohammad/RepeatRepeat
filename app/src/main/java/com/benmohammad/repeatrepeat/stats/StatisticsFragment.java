package com.benmohammad.repeatrepeat.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benmohammad.repeatrepeat.stats.domain.StatisticsEvent;
import com.benmohammad.repeatrepeat.stats.domain.StatisticsState;
import com.benmohammad.repeatrepeat.stats.view.StatisticsViews;
import com.google.common.base.Optional;
import com.spotify.mobius.MobiusLoop;

import static com.benmohammad.repeatrepeat.stats.StatisticsInjector.createController;
import static com.benmohammad.repeatrepeat.stats.StatisticsStateBundler.bundleToStatisticsState;
import static com.benmohammad.repeatrepeat.stats.StatisticsStateBundler.statisticsStateToBundle;
import static com.benmohammad.repeatrepeat.stats.effecthandlers.StatisticsEffectHandlers.createEffectHandler;
import static com.spotify.mobius.internal_util.Preconditions.checkNotNull;

public class StatisticsFragment extends Fragment {

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    private MobiusLoop.Controller<StatisticsState, StatisticsEvent> controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatisticsViews views = new StatisticsViews(inflater, checkNotNull(container));
        controller =
                createController(
                        createEffectHandler(
                                getContext()), bundleToStatisticsState(savedInstanceState));
        controller.connect(views);
        return views.getRootView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Optional<Bundle> optionalBundle =statisticsStateToBundle(controller.getModel());
        if(optionalBundle.isPresent()) {
            outState.putBundle("statistics", optionalBundle.get());
        }
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
}
