package com.linkx.babycare.view.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.BuruDetail;
import com.linkx.babycare.data.services.BuruDataRestoreService;
import com.linkx.babycare.data.services.BuruDataUpdateService;
import com.linkx.babycare.utils.IOUtil;
import com.linkx.babycare.utils.TextUtil;
import com.linkx.babycare.view.listeners.OnItemAddedListener;
import rx.Subscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class BuruDetailView extends ViewWithBackgroundThread {
    @Bind(R.id.breast_which)
    TextView breastWhich;
    @Bind(R.id.buru_timer)
    TextView buruTimer;
    @Bind(R.id.buru_controll)
    ImageView buruControll;
    @Bind(R.id.buru_start_time)
    TextView buruStartTime;
    @Bind(R.id.buru_end_time)
    TextView buruEndTime;

    private Handler handler = new Handler();
    private long startTime = 0;
    private long endTime = 0;
    private String tag = "";
    private boolean running = false;
    private Drawable backgroud;

    private Runnable timerUpdater =  new Runnable() {
        @Override
        public void run() {
            long curTime = System.currentTimeMillis();
            long duration = curTime - startTime;
            buruTimer.setText(TextUtil.formatSeconds(TimeUnit.MILLISECONDS.toSeconds(duration)));
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));

        }
    };

    private OnItemAddedListener<BuruDetail> onItemAddedListener;

    public BuruDetailView(Context context) {
        super(context);
        setup(null);
    }

    public BuruDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public BuruDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    @Override
    public String name() {
        return BuruDetailView.class.getName();
    }

    public void setOnItemAddedListener(OnItemAddedListener<BuruDetail> onItemAddedListener) {
        this.onItemAddedListener = onItemAddedListener;
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_buru_detail, this);
        ButterKnife.bind(this);
        if (null != attrs) {
            String namespace = "http://schemas.android.com/apk/res-auto";
            tag = attrs.getAttributeValue(namespace, "tag");
        }

        breastWhich.setText(tag);
        backgroud = buruTimer.getBackground();
        setupListeners();
    }

    private void updateControllIcon() {
        if (running) {
            buruControll.setImageResource(R.drawable.ic_action_playback_stop);
        } else {
            buruControll.setImageResource(R.drawable.ic_action_playback_play);
        }
    }

    private void setupListeners() {

        restore();

        buruControll.setOnClickListener(v -> {
            if (running) {
                onStop();
                cleanup();
            } else {
                onStart();
                backup();
            }
        });
    }

    private void onStart() {
        long startTime = System.currentTimeMillis();
        startRecord(startTime);
    }

    private void startRecord(long startTime) {
        running = true;
        updateControllIcon();
        this.startTime = startTime;
        long curTime = System.currentTimeMillis();
        long duration = curTime - startTime;
        buruTimer.setText(TextUtil.formatSeconds(TimeUnit.MILLISECONDS.toSeconds(duration)));
        buruTimer.setBackgroundResource(R.color.accent);
        buruStartTime.setText(TextUtil.formatTime(startTime));
        buruEndTime.setText("--:--");
        handler.postDelayed(timerUpdater, TimeUnit.SECONDS.toMillis(1));
    }


    private void backup() {
        String fileName = IOUtil.tmpFileName("buru");
        Map<String, Object> bundle = new HashMap<>();
        bundle.put("tag", tag);
        bundle.put("startTime", startTime);
        new BuruDataRestoreService().backup(fileName, bundle, backgroundLooper);
    }

    private void cleanup() {
        String fileName = IOUtil.tmpFileName("buru");
        new BuruDataRestoreService().cleanup(fileName, backgroundLooper);
    }

    private void restore() {
        String fileName = IOUtil.tmpFileName("buru");
        new BuruDataRestoreService().restore(fileName, backgroundLooper)
                .subscribe(new Subscriber<Map>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map stringObjectMap) {
                        String tagFound = (String)stringObjectMap.get("tag");
                        Long startTime = (Long)stringObjectMap.get("startTime");
                        Log.d("Buru", "tagExpected=" + tag + ",tagFound=" + tagFound + ",startTime=" +  startTime);
                        if (!tag.equals(tagFound) || startTime == null) {
                            return;
                        }
                        startRecord(startTime);
                    }
                });
    }

    private void onStop() {
        running = false;
        updateControllIcon();
        endTime = System.currentTimeMillis();
        buruEndTime.setText(TextUtil.formatTime(endTime));
        handler.removeCallbacks(timerUpdater);
        buruTimer.setBackground(backgroud);

        // TODO
        String fileName = IOUtil.dataFileName("buru", startTime);
        BuruDetail detail = BuruDetail.create(UUID.randomUUID().toString(), startTime, endTime, tag);
        new BuruDataUpdateService().updateFromUISliently(fileName, detail, backgroundLooper);

        if (null != onItemAddedListener) {
            onItemAddedListener.onItemAdded(detail);
        }

    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("BuruDetailSave-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }
}
