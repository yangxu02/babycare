package com.linkx.babycare.view.dialogs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.babycare.R;
import com.linkx.babycare.data.models.BuruDetail;
import com.linkx.babycare.data.models.DabianDetail;
import com.linkx.babycare.data.models.Quantity;
import com.linkx.babycare.data.models.Softy;
import com.linkx.babycare.utils.TextUtil;
import com.linkx.babycare.view.listeners.OnItemAddedListener;

import java.util.UUID;

/**
 * Created by ulyx.yang on 2016/9/17.
 */
public class DabianEditDialog extends DialogFragment {
    @Bind(R.id.title)
    TextView titleText;
    @Bind(R.id.color)
    RadioGroup colorSelector;
    @Bind(R.id.day)
    EditText dayText;
    @Bind(R.id.time)
    EditText timeText;
    @Bind(R.id.softy)
    RadioGroup softySelector;
    @Bind(R.id.quantity)
    RadioGroup quantitySelector;
    @Bind(R.id.save)
    ImageButton save;
    @Bind(R.id.cancel)
    ImageButton cancel;
    @Bind(R.id.error)
    TextView errorText;

    private String title;

    private OnItemAddedListener<DabianDetail> onItemAddedListener;

    public DabianEditDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_dabian, container);
        ButterKnife.bind(this, view);

        titleText.setText(title);
        dayText.setText(TextUtil.formatDay(System.currentTimeMillis()));
        timeText.setText(TextUtil.formatTime(System.currentTimeMillis()));

        cancel.setOnClickListener(v -> this.dismiss());
        save.setOnClickListener(v -> {
            int colorSelected = colorSelector.getCheckedRadioButtonId();

            int color = Color.YELLOW;
            switch (colorSelected) {
                case R.id.black:
                    color = Color.BLACK;
                    break;
                case R.id.green:
                    color = Color.GREEN;
                    break;
            }

            int quantitySelected = quantitySelector.getCheckedRadioButtonId();
            Quantity quantity = Quantity.appropriate;
              switch (quantitySelected) {
                case R.id.quantity_few:
                    quantity = Quantity.few;
                    break;
                case R.id.quantity_lot:
                    quantity = Quantity.lot;
                    break;
            }

            int softySelected = softySelector.getCheckedRadioButtonId();
            Softy softy = Softy.appropriate;
              switch (softySelected) {
                case R.id.softy_soft:
                    softy = Softy.soft;
                    break;
                case R.id.softy_hard:
                    softy = Softy.hard;
                    break;
            }

            String day = dayText.getText().toString();
            String time = timeText.getText().toString();
            long timeMillis = TextUtil.parseTime(day, time);

            if (-1 == timeMillis) {
                errorText.setText(R.string.dialog_error_time_field);
                return;
            }
            if (null != onItemAddedListener) {
                DabianDetail detail = DabianDetail.create(UUID.randomUUID().toString(), timeMillis, color, quantity, softy);
                onItemAddedListener.onItemAdded(detail);
            }
            this.dismiss();
        });

        return view;
    }

    public void setOnItemAddedListener(OnItemAddedListener<DabianDetail> onItemAddedListener) {
        this.onItemAddedListener = onItemAddedListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
