package com.linkx.babycare.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;

public class HistoryDataDayView extends FrameLayout {
    @Bind(R.id.data_day)
    TextView dayOfData;
    @Bind(R.id.data_container)
    ViewGroup containerOfData;

    public HistoryDataDayView(Context context) {
        super(context);
        setup(null);
    }

    public HistoryDataDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public HistoryDataDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_history_data_day, this);
        ButterKnife.bind(this);
    }

    public HistoryDataDayView setDay(String day) {
        dayOfData.setText(day);
        return this;
    }

    public HistoryDataDayView removeAllDatas() {
        containerOfData.removeAllViews();
        return this;
    }

    public HistoryDataDayView addData(View view) {
        containerOfData.addView(view);
        return this;
    }

}
