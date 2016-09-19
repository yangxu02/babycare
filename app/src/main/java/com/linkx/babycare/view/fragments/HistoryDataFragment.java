package com.linkx.babycare.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.services.BuruDataQueryService;
import com.linkx.babycare.view.components.*;
import rx.functions.Func1;

import java.util.List;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class HistoryDataFragment extends Fragment {

    public static HistoryDataFragment newInstance() {
        HistoryDataFragment fragment = new HistoryDataFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
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
        new BuruDataQueryService().queryHistoryAndDisplay(backgroundLooper, segments,
                allDetail -> {
                    HistoryDataDayView viewOfDay = new HistoryDataDayView(context);
                    viewOfDay.setDay(allDetail.day());
                    viewOfDay.addData(setupSegmentView("哺乳", allDetail.buruDetails(),
                            detail -> new BuruDetailListItemView(context).setupViews(detail).hideDeleteIcon())
                    );
                    viewOfDay.addData(setupSegmentView("大便", allDetail.dabianDetails(),
                            detail -> new DabianDetailListItemView(context).setupViews(detail).hideDeleteIcon())
                    );
                    viewOfDay.addData(setupSegmentView("小便", allDetail.xiaobianDetails(),
                            detail -> new SimpleDetailListItemView(context).setupViews(detail).hideDeleteIcon())
                    );
                    viewOfDay.addData(setupSegmentView("黄疸", allDetail.huangdan(),
                            detail -> new SimpleDetailListItemView(context).setupViews(detail).hideDeleteIcon())
                    );
                    viewOfDay.addData(setupSegmentView("体重", allDetail.tizhong(),
                            detail -> new SimpleDetailListItemView(context).setupViews(detail).hideDeleteIcon())
                    );
                    viewOfDay.addData(setupSegmentView("体温", allDetail.tiwen(),
                            detail -> new SimpleDetailListItemView(context).setupViews(detail).hideDeleteIcon())
                    );

                    return viewOfDay;
                });
    }

    private <T> View setupSegmentView(String title, List<T> details, Func1<T, View> func) {
        Context context = getActivity();
        HistoryDataSegmentView segmentView = new HistoryDataSegmentView(context);
        segmentView.setTitle(title);
        segmentView.removeAllRows();
        for (T detail : details) {
            segmentView.addRow(func.call(detail));
        }
        return segmentView;
    }


    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("HistoryDetail-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }
}
