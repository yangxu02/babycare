package com.linkx.babycare.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.common.collect.Lists;
import com.linkx.babycare.R;
import com.linkx.babycare.utils.DisplayUtils;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

public class HistoryChartSegmentView extends FrameLayout {
    @Bind(R.id.segment_title)
    TextView segmentTitle;

    public HistoryChartSegmentView(Context context) {
        super(context);
        setup(null);
    }

    public HistoryChartSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public HistoryChartSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_history_chart_segment, this);
        ButterKnife.bind(this);
    }

    public HistoryChartSegmentView setTitle(String title) {
        segmentTitle.setText(title);
        segmentTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        return this;
    }

    public HistoryChartSegmentView addData(List<Pair<String, Float>> dataSet, float[] yAxis) {
        List<PointValue> values = new ArrayList<PointValue>();
        List<AxisValue> axisXValues = new ArrayList<AxisValue>();
//        float min = Float.MAX_VALUE;
//        float max = Float.MIN_VALUE;
        for (int i = 0; i < dataSet.size(); ++i) {
            Pair<String, Float> pair = dataSet.get(i);
            values.add(new PointValue(i, pair.second));
            axisXValues.add(new AxisValue(i).setLabel(pair.first));
//            min = Math.min(min, pair.second);
//            max = Math.max(max, pair.second);
            Log.d("Chart", "add point[" + i + "]=(" + pair.first + "," + pair.second + ")");
        }

        int yNums = 5;
//        min -= Math.round(0.2 * min);
//        max += Math.round(0.2 * max);
//        float gap = (max - min) / yNums;
//        Log.d("Chart", "min=" + min + ",max=" + max + ",gap=" + gap);
        List<AxisValue> axisYValues = new ArrayList<AxisValue>();
        for (int i = 0; i < yAxis.length; ++i) {
            axisYValues.add(new AxisValue(yAxis[i]).setLabel("" + yAxis[i]));
        }
        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_ORANGE);
        line.setPointRadius(2);
        line.setStrokeWidth(1);
        line.setFilled(true);
        line.setCubic(false);
        line.setHasLines(true);
        line.setHasPoints(true);
//        line.setHasLabels(true);
        LineChartData chartData = new LineChartData(Lists.newArrayList(line));
        chartData.setAxisXBottom(new Axis(axisXValues).setHasLines(true).setHasTiltedLabels(true));
        chartData.setAxisYLeft(new Axis(axisYValues).setHasLines(true));
        chartData.setValueLabelBackgroundEnabled(true);
        LineChartView chartView = new LineChartView(getContext());
        chartView.setLineChartData(chartData);

        // For build-up animation you have to disable viewport recalculation.
        chartView.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, yAxis[yAxis.length - 1], dataSet.size(), yAxis[0]);
        chartView.setMaximumViewport(v);
        chartView.setCurrentViewport(v);

        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chartView.setScrollEnabled(true);

//        segmentChart.addView(chartView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DisplayUtils.dpToPx(getContext(), 240));
        layoutParams.bottomMargin = DisplayUtils.dpToPx(getContext(), 32);
        layoutParams.topMargin = DisplayUtils.dpToPx(getContext(), 16);
        layoutParams.leftMargin = DisplayUtils.dpToPx(getContext(), 8);
        layoutParams.rightMargin = DisplayUtils.dpToPx(getContext(), 8);
        this.addView(chartView, layoutParams);
        return this;
    }


}
