package com.linkx.babycare.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.AllDetail;
import com.linkx.babycare.data.models.SimpleDetail;
import com.linkx.babycare.data.services.BuruDataQueryService;
import com.linkx.babycare.view.components.*;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.SafeSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class HistoryChartFragment extends Fragment {

    public static HistoryChartFragment newInstance() {
        HistoryChartFragment fragment = new HistoryChartFragment();
        Bundle stats = new Bundle();
        // pass
        fragment.setArguments(stats);
        return fragment;
    }

    @Bind(R.id.segments)
    ViewGroup segments;

    private Looper backgroundLooper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        ButterKnife.bind(this, view);
        setupViews();
        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void setupViews() {
        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();
        final Context context = getActivity();
        new BuruDataQueryService().queryHistory(backgroundLooper).subscribe(new Subscriber<List<AllDetail>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            private final Func1<List<SimpleDetail>, Float> average = simpleDetails -> {
                if (null == simpleDetails || simpleDetails.isEmpty()) return 0f;
                float average1 = 0;
                int n = 0;
                for (SimpleDetail simpleDetail : simpleDetails) {
                    average1 += Float.parseFloat(simpleDetail.value());
                    ++n;
                }
                return average1 / n;
            };

            @Override
            public void onNext(List<AllDetail> allDetails) {
                List<Pair<String, Float>> tempes = new ArrayList<>();
                List<Pair<String, Float>> weights = new ArrayList<>();
                for (AllDetail detail : allDetails) {
                    tempes.add(new Pair<>((detail.day()), average.call(detail.tiwen())));
                    weights.add(new Pair<>((detail.day()), average.call(detail.tizhong())));
                }

                segments.addView(new HistoryChartSegmentView(getActivity()).setTitle("平均体温").addData(tempes, new float[] {36, 36.5f, 37f, 37.5f, 38f, 38.5f, 39}));
                segments.addView(new HistoryChartSegmentView(getActivity()).setTitle("平均体重").addData(weights, new float[] {3, 3.25f, 3.5f, 3.75f, 4f, 4.25f, 4.5f, 4.75f, 5f}));
            }
        });
    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("HistoryDetail-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }
}
