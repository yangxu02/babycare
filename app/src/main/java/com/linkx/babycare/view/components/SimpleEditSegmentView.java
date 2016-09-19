package com.linkx.babycare.view.components;

import android.content.Context;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.SimpleDetail;
import com.linkx.babycare.data.services.BuruDataQueryService;
import com.linkx.babycare.data.services.BuruDataUpdateService;
import com.linkx.babycare.utils.IOUtil;
import com.linkx.babycare.view.dialogs.SimpleEditDialog;

public class SimpleEditSegmentView extends FrameLayout {
    @Bind(R.id.header)
    SegmentHeaderView header;
    @Bind(R.id.data)
    ViewGroup dataContainer;

    private String title;
    private String type;
    private boolean hideValueField;
    private String valueFieldName;
    private Looper backgroundLooper;

    public SimpleEditSegmentView(Context context) {
        super(context);
        setup(null);
    }

    public SimpleEditSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public SimpleEditSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_simple_edit_segment, this);
        ButterKnife.bind(this);
    }

    private void setupViews() {
        Context context = getContext();
        header.setTitle(title);
        header.setOnItemAddedListener(v -> {
            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
            SimpleEditDialog dialog = new SimpleEditDialog();

            dialog.setValueName(valueFieldName);
            dialog.setHideValueField(hideValueField);
            dialog.setTitle(title);
            dialog.setType(type);
            dialog.show(fm, "");
            dialog.setOnItemAddedListener(item -> {
                String fileName = IOUtil.dataFileName(type, item.timestamp());
                new BuruDataUpdateService().updateFromUISliently(fileName, item, backgroundLooper);
                dataContainer.addView(new SimpleDetailListItemView(context).setupViews(item));
            });
        });

        dataContainer.removeAllViews();
        new BuruDataQueryService().queryAndDisplay(type, SimpleDetail.class, backgroundLooper, dataContainer,
                detail -> new SimpleDetailListItemView(context).setupViews(detail));

    }

    public void setAttrs(String type, String title, Looper looper) {
        setAttrs(type, title, true, "", looper);
    }

    public void setAttrs(String type, String title, String valueFieldName, Looper looper) {
        setAttrs(type, title, false, valueFieldName, looper);
    }

    private void setAttrs(String type, String title, boolean hideValueField, String valueFieldName, Looper looper) {
        this.type = type;
        this.title = title;
        this.hideValueField = hideValueField;
        this.valueFieldName = valueFieldName;
        this.backgroundLooper = looper;
        this.setupViews();
    }

    public ViewGroup getDataContainer() {
        return dataContainer;
    }
}
