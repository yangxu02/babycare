package com.linkx.babycare.view.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.BuruDetail;
import com.linkx.babycare.data.models.SimpleDetail;
import com.linkx.babycare.utils.TextUtil;
import com.linkx.babycare.view.listeners.OnItemAddedListener;

import java.util.UUID;

/**
 * Created by ulyx.yang on 2016/9/17.
 */
public class SimpleEditDialog extends DialogFragment {
    @Bind(R.id.title)
    TextView titleText;
    @Bind(R.id.value_name)
    TextView valueNameText;
    @Bind(R.id.value)
    EditText valueText;
    @Bind(R.id.day)
    EditText dayText;
    @Bind(R.id.time)
    EditText timeText;
    @Bind(R.id.save)
    ImageButton save;
    @Bind(R.id.cancel)
    ImageButton cancel;
    @Bind(R.id.error)
    TextView errorText;

    private String title;
    private String type;
    private String valueName;
    private boolean hideValueField = true;

    private OnItemAddedListener<SimpleDetail> onItemAddedListener;

    public SimpleEditDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_simple, container);
        ButterKnife.bind(this, view);
        titleText.setText(title);
        if (hideValueField) {
            this.hideValueField();
        } else {
            valueNameText.setText(valueName);
        }
        valueText.setText("");
        dayText.setText(TextUtil.formatDay(System.currentTimeMillis()));
        timeText.setText(TextUtil.formatTime(System.currentTimeMillis()));

        cancel.setOnClickListener(v -> this.dismiss());
        save.setOnClickListener(v -> {
            String day = dayText.getText().toString();
            String time = timeText.getText().toString();
            String value = valueText.getText().toString();
            long timeMillis = TextUtil.parseTime(day, time);

            if (-1 == timeMillis) {
                errorText.setText(R.string.dialog_error_time_field);
                return;
            }
            if (null != onItemAddedListener) {
                SimpleDetail detail = SimpleDetail.create(UUID.randomUUID().toString(), timeMillis, type, value);
                onItemAddedListener.onItemAdded(detail);
            }
            this.dismiss();
        });

        return view;
    }

    public void setOnItemAddedListener(OnItemAddedListener<SimpleDetail> onItemAddedListener) {
        this.onItemAddedListener = onItemAddedListener;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
        this.hideValueField = false;
    }

    public void setHideValueField(boolean hideValueField) {
        this.hideValueField = hideValueField;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void hideValueField() {
        valueNameText.setVisibility(View.INVISIBLE);
        valueText.setVisibility(View.INVISIBLE);
    }
}
