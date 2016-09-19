package com.linkx.babycare.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.BuruDetail;
import com.linkx.babycare.data.models.DabianDetail;
import com.linkx.babycare.data.models.Model;
import com.linkx.babycare.data.models.SimpleDetail;
import com.linkx.babycare.data.services.BuruDataQueryService;
import com.linkx.babycare.data.services.BuruDataUpdateService;
import com.linkx.babycare.utils.IOUtil;
import com.linkx.babycare.view.components.*;
import com.linkx.babycare.view.dialogs.BuruEditDialog;
import com.linkx.babycare.view.dialogs.DabianEditDialog;
import com.linkx.babycare.view.dialogs.SimpleEditDialog;
import com.linkx.babycare.view.listeners.OnItemAddedListener;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.List;
import java.util.UUID;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class EditFragment extends Fragment {

    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        Bundle stats = new Bundle();
        // pass
        fragment.setArguments(stats);
        return fragment;
    }

    @Bind(R.id.buru_header)
    SegmentHeaderView headerBuru;
    @Bind(R.id.buru_left)
    BuruDetailView buruLeft;
    @Bind(R.id.buru_right)
    BuruDetailView buruRight;
    @Bind(R.id.buru_data)
    LinearLayout buruDataContainer;

    @Bind(R.id.dabian_header)
    SegmentHeaderView headerDabian;
    @Bind(R.id.dabian_data)
    LinearLayout dabianDataContainer;

    @Bind(R.id.xiaobian_view)
    SimpleEditSegmentView viewXiaobian;

    @Bind(R.id.huangdan_view)
    SimpleEditSegmentView viewHuangdan;

    @Bind(R.id.tiwen_view)
    SimpleEditSegmentView viewTiwen;

    @Bind(R.id.tizhong_view)
    SimpleEditSegmentView viewTizhong;

    private Looper backgroundLooper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buru, container, false);
        ButterKnife.bind(this, view);
        setupViews();
        return view;
    }

    @Override
    public void onDestroyView() {
        // TODO save timer
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void setupViews() {

        Context context = getActivity();

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        setupHeaders();

        setupContainers();

        OnItemAddedListener<BuruDetail> onItemAddedListener = item -> {
            BuruDetailListItemView itemView = new BuruDetailListItemView(getActivity()).setupViews(item);
            buruDataContainer.addView(itemView);
        };
        buruLeft.setOnItemAddedListener(onItemAddedListener);
        buruRight.setOnItemAddedListener(onItemAddedListener);
    }

    private void setupContainers() {
        Context context = getActivity();
        BuruDataQueryService queryService = new BuruDataQueryService();

        buruDataContainer.removeAllViews();
        queryService.queryAndDisplay("buru", BuruDetail.class, backgroundLooper, buruDataContainer,
                detail -> new BuruDetailListItemView(context).setupViews(detail));

        dabianDataContainer.removeAllViews();
        queryService.queryAndDisplay("dabian", DabianDetail.class, backgroundLooper, dabianDataContainer,
                detail -> new DabianDetailListItemView(context).setupViews(detail));

    }

    private void setupBuruHeader(String title, String type,
                                 SegmentHeaderView header, ViewGroup container) {
        Context context = getActivity();
        header.setTitle(title);
        header.setOnItemAddedListener(v -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            BuruEditDialog dialog = new BuruEditDialog();
            dialog.setTitle(title);
            dialog.show(fm, "");
            dialog.setOnItemAddedListener(item -> {
                String fileName = IOUtil.dataFileName(type, item.startTime());
                new BuruDataUpdateService().updateFromUISliently(fileName, item, backgroundLooper);
                container.addView(new BuruDetailListItemView(context).setupViews(item));
            });
        });
    }

    private void setupDabianHeader(String title, String type,
                                   SegmentHeaderView header, ViewGroup container) {
        Context context = getActivity();
        header.setTitle(title);
        header.setOnItemAddedListener(v -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            DabianEditDialog dialog = new DabianEditDialog();
            dialog.setTitle(title);
            dialog.show(fm, "");
            dialog.setOnItemAddedListener(item -> {
                String fileName = IOUtil.dataFileName(type, item.timestamp());
                new BuruDataUpdateService().updateFromUISliently(fileName, item, backgroundLooper);
                container.addView(new DabianDetailListItemView(context).setupViews(item));
            });
        });

    }

    private void setupHeaders() {
        setupBuruHeader("哺乳", "buru", headerBuru, buruDataContainer);
        setupDabianHeader("大便", "dabian", headerDabian, dabianDataContainer);

        viewXiaobian.setAttrs("xiaobian", "小便", backgroundLooper);
        viewHuangdan.setAttrs("huangdan", "黄疸", backgroundLooper);
        viewTizhong.setAttrs("tizhong", "体重", "体重", backgroundLooper);
        viewTiwen.setAttrs("tiwen", "体温", "体温", backgroundLooper);
    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("BuruDetail-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }
}
