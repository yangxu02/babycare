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
import com.linkx.babycare.utils.TextUtil;
import com.linkx.babycare.view.listeners.OnItemAddedListener;

import java.util.UUID;

/**
 * Created by ulyx.yang on 2016/9/17.
 */
public class BuruEditDialog extends DialogFragment {
    @Bind(R.id.title)
    TextView titleText;
    @Bind(R.id.location)
    RadioGroup locationSelector;
    @Bind(R.id.left)
    RadioButton left;
    @Bind(R.id.right)
    RadioButton right;
    @Bind(R.id.day)
    EditText dayText;
    @Bind(R.id.start_time)
    EditText startTimeText;
    @Bind(R.id.end_time)
    EditText endTimeText;
    @Bind(R.id.save)
    ImageButton save;
    @Bind(R.id.cancel)
    ImageButton cancel;
    @Bind(R.id.error)
    TextView errorText;

    private String title;
    private OnItemAddedListener<BuruDetail> onItemAddedListener;

    public BuruEditDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_buru, container);
        ButterKnife.bind(this, view);

        titleText.setText(title);
        dayText.setText(TextUtil.formatDay(System.currentTimeMillis()));
        cancel.setOnClickListener(v -> this.dismiss());
        save.setOnClickListener(v -> {
            int selected = locationSelector.getCheckedRadioButtonId();
            String location = (selected == R.id.left ? left.getText().toString() : right.getText().toString());
            String day = dayText.getText().toString();
            String startTime = startTimeText.getText().toString();
            String endTime = endTimeText.getText().toString();
            long startMillis = TextUtil.parseTime(day, startTime);
            long endMillis = TextUtil.parseTime(day, endTime);

            if (-1 == startMillis || -1 == endMillis) {
                errorText.setText(R.string.dialog_error_time_range);
                return;
            }
            if (null != onItemAddedListener) {
                BuruDetail detail = BuruDetail.create(UUID.randomUUID().toString(), startMillis, endMillis, location);
                onItemAddedListener.onItemAdded(detail);
            }
            this.dismiss();
        });

        return view;
    }

    public void setOnItemAddedListener(OnItemAddedListener<BuruDetail> onItemAddedListener) {
        this.onItemAddedListener = onItemAddedListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
