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

import java.util.List;

public class HistoryDataSegmentView extends FrameLayout {
    @Bind(R.id.segment_title)
    TextView segmentTitle;
    @Bind(R.id.segment_rows)
    ViewGroup segmentRows;

    public HistoryDataSegmentView(Context context) {
        super(context);
        setup(null);
    }

    public HistoryDataSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public HistoryDataSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_history_data_segment, this);
        ButterKnife.bind(this);
    }

    public HistoryDataSegmentView setTitle(String title) {
        segmentTitle.setText(title);
        return this;
    }

    public HistoryDataSegmentView addRow(View view) {
        segmentRows.addView(view);
        return this;
    }

    public HistoryDataSegmentView removeAllRows() {
        segmentRows.removeAllViews();
        return this;
    }

    public HistoryDataSegmentView addRows(List<View> views) {
        for (View view : views) {
            segmentRows.addView(view);
        }
        return this;
    }


}
