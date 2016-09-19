package com.linkx.babycare.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.view.listeners.OnItemAddedListener;

public class SegmentHeaderView extends FrameLayout {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.add)
    ImageView add;

    private OnItemAddedListener<Void> onItemAddedListener;

    public SegmentHeaderView(Context context) {
        super(context);
        setup(null);
    }

    public SegmentHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public SegmentHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_segment_header, this);
        ButterKnife.bind(this);
        setupListeners();
    }

    private void setupListeners() {
        add.setOnClickListener(v -> {
            if (null == onItemAddedListener) return;
            onItemAddedListener.onItemAdded(null);
        });
    }

    public SegmentHeaderView setOnItemAddedListener(OnItemAddedListener<Void> onItemAddedListener) {
        this.onItemAddedListener = onItemAddedListener;
        return this;
    }

    public SegmentHeaderView setTitle(String title) {
       this.title.setText(title);
       return this;
    }

}
