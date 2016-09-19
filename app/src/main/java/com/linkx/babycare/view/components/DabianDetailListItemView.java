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
import com.linkx.babycare.data.models.DabianDetail;
import com.linkx.babycare.data.services.BuruDataUpdateService;
import com.linkx.babycare.utils.IOUtil;
import com.linkx.babycare.utils.TextUtil;

public class DabianDetailListItemView extends ViewWithBackgroundThread {
    @Bind(R.id.dabian_color)
    View dabianColor;
    @Bind(R.id.dabian_time)
    TextView dabianTime;
    @Bind(R.id.softy)
    TextView softyText;
    @Bind(R.id.quantity)
    TextView quantityText;
    @Bind(R.id.dabian_rating)
    ImageView dabianRating;
    @Bind(R.id.dabian_delete)
    ImageView dabianDelete;

    private DabianDetail detail;

    public DabianDetailListItemView(Context context) {
        super(context);
        setup(null);
    }

    public DabianDetailListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public DabianDetailListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    @Override
    public String name() {
        return DabianDetailListItemView.class.getName();
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.list_item_dabian_detail, this);
        ButterKnife.bind(this);

        setupListeners();
    }

    private void setupListeners() {

        dabianDelete.setOnClickListener(v -> {
            // row is your row, the parent of the clicked button, this?
            View row = (View) v.getParent();
            // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
            ViewGroup container = ((ViewGroup)row.getParent());
            // delete the row and invalidate your view so it gets redrawn
            container.removeView(row);
            container.invalidate();

            long timestamp = detail.timestamp();
            String fileName = IOUtil.dataFileName("dabian.deleted", timestamp);
            new BuruDataUpdateService().updateFromUISliently(fileName, detail, backgroundLooper);
        });
    }

   public DabianDetailListItemView setupViews(DabianDetail detail) {
       this.detail = detail;
       dabianColor.setBackgroundColor(detail.color());
       dabianTime.setText(TextUtil.formatTime(detail.timestamp()));
       softyText.setText(detail.softy().getResId());
       quantityText.setText(detail.quantity().getResId());
       // TODO
       return this;
    }

     public DabianDetailListItemView hideDeleteIcon() {
        dabianDelete.setClickable(false);
        dabianDelete.setVisibility(INVISIBLE);
        return this;
    }


}
