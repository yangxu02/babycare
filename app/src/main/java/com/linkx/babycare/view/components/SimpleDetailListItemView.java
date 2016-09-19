package com.linkx.babycare.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.common.base.Strings;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.SimpleDetail;
import com.linkx.babycare.data.services.BuruDataUpdateService;
import com.linkx.babycare.utils.IOUtil;
import com.linkx.babycare.utils.TextUtil;

public class SimpleDetailListItemView extends ViewWithBackgroundThread {
    @Bind(R.id.record_time)
    TextView recordTime;
    @Bind(R.id.value)
    TextView value;
    @Bind(R.id.rating)
    ImageView rating;
    @Bind(R.id.delete)
    ImageView delete;

    private SimpleDetail detail;

    public SimpleDetailListItemView(Context context) {
        super(context);
        setup(null);
    }

    public SimpleDetailListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public SimpleDetailListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    @Override
    public String name() {
        return SimpleDetailListItemView.class.getName();
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.list_item_simple_detail, this);
        ButterKnife.bind(this);

        setupListeners();
    }

    private void setupListeners() {

        delete.setOnClickListener(v -> {
            // row is your row, the parent of the clicked button, this?
            View row = (View) v.getParent();
            // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
            ViewGroup container = ((ViewGroup)row.getParent());
            // delete the row and invalidate your view so it gets redrawn
            container.removeView(row);
            container.invalidate();
            long timestamp = detail.timestamp();
            String tag = detail.type() + ".deleted";
            String fileName = IOUtil.dataFileName(tag, timestamp);
            new BuruDataUpdateService().updateFromUISliently(fileName, detail, backgroundLooper);
        });
    }

   public SimpleDetailListItemView setupViews(SimpleDetail detail) {
       this.detail = detail;
       if (!Strings.isNullOrEmpty(detail.value())) {
           value.setText(detail.value());
       }
       recordTime.setText(TextUtil.formatTime(detail.timestamp()));
       return this;
    }

    public SimpleDetailListItemView hideDeleteIcon() {
        delete.setClickable(false);
        delete.setVisibility(INVISIBLE);
        return this;
    }

}
