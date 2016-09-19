package com.linkx.babycare.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.BuruDetail;
import com.linkx.babycare.data.services.BuruDataUpdateService;
import com.linkx.babycare.utils.IOUtil;
import com.linkx.babycare.utils.TextUtil;

import java.util.concurrent.TimeUnit;

public class BuruDetailListItemView extends ViewWithBackgroundThread {
    @Bind(R.id.breast_which)
    TextView breastWhich;
    @Bind(R.id.buru_start_time)
    TextView buruStartTime;
    @Bind(R.id.buru_end_time)
    TextView buruEndTime;
    @Bind(R.id.buru_duration)
    TextView buruDuration;
    @Bind(R.id.buru_rating)
    ImageView buruRating;
    @Bind(R.id.buru_delete)
    ImageView buruDelete;

    private BuruDetail detail;

    public BuruDetailListItemView(Context context) {
        super(context);
        setup(null);
    }

    public BuruDetailListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public BuruDetailListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    @Override
    public String name() {
        return BuruDetailListItemView.class.getName();
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.list_item_buru_detail, this);
        ButterKnife.bind(this);

        setupListeners();
    }

    private void setupListeners() {

        buruDelete.setOnClickListener(v -> {
            // row is your row, the parent of the clicked button, this?
            View row = (View) v.getParent();
            // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
            ViewGroup container = ((ViewGroup)row.getParent());
            // delete the row and invalidate your view so it gets redrawn
            container.removeView(row);
            container.invalidate();

            // TODO add a delete log
            long timestamp = detail.startTime();
            String fileName = IOUtil.dataFileName("buru.deleted", timestamp);
            new BuruDataUpdateService().updateFromUISliently(fileName, detail, backgroundLooper);
        });
    }

   public BuruDetailListItemView setupViews(BuruDetail detail) {
       this.detail = detail;
       breastWhich.setText(detail.tag());
       buruStartTime.setText(TextUtil.formatTime(detail.startTime()));
       buruEndTime.setText(TextUtil.formatTime(detail.endTime()));
       long duration = TimeUnit.MILLISECONDS.toSeconds(detail.endTime() - detail.startTime());
       buruDuration.setText(TextUtil.formatSeconds(duration));
       if (duration < TimeUnit.MINUTES.toSeconds(5)) {
           buruRating.setImageResource(R.drawable.ic_action_emo_cry);
       }
       return this;
    }

    public BuruDetailListItemView hideDeleteIcon() {
        buruDelete.setClickable(false);
        buruDelete.setVisibility(INVISIBLE);
        return this;
    }

}
