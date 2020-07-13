package com.example.moneymanager.view.bottom_menu;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.moneymanager.R;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.model.SpendCategory;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements OnChartValueSelectedListener, View.OnTouchListener{

    //private PieChart pieChart;

    //tring[] months = {"Июнь", "Июль", "Август"};
    //int[] spends = {20000, 15000, 30000};

    private PieChart chart;
    private List<SpendCategory> spendCategoryList = new ArrayList<>();
    private float dX;
    private float dY;
    int lastAction;
    //private SeekBar seekBarX, seekBarY;
    //private TextView tvX, tvY;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        final View dragView = root.findViewById(R.id.fab);
        dragView.setOnTouchListener(this);

        //tvX = root.findViewById(R.id.tvXMax);
        //tvY = root.findViewById(R.id.tvYMax);

        //seekBarX = root.findViewById(R.id.seekBar1);
        //seekBarY = root.findViewById(R.id.seekBar2);

        //seekBarX.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) MainFragment.this);
        //seekBarY.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) MainFragment.this);

        try {
            spendCategoryList = DB.GetSpendCategories();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < spendCategoryList.size(); i++) {
            spendCategoryList.get(i).spend = i * 10 + 10;
        }

        chart = root.findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(R.color.colorBackground);

        chart.setTransparentCircleColor(Color.BLACK);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        //seekBarX.setProgress(4);
        //seekBarY.setProgress(10);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        /*Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(15f);
        l.setYEntrySpace(7f);
        l.setYOffset(0f);
        l.setTextSize(20f);*/

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);
        setData(spendCategoryList);
        return root;
    }

    /*public void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        ArrayList<PieEntry> entries = new ArrayList<>();
        PieData data = new PieData();
        /*Pie pie = AnyChart.pie();
        List<DataEntry> dataEntryList = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            dataEntryList.add(new ValueDataEntry(months[i], spends[i]));
        }
        pie.title("Расходы");
        pie.data(dataEntryList);
        pie.bounds(0, 0, 400d, 400d);
        pie.labels().position("outside");
        pie.legend().title().enabled(false);
        pie.legend().title()
                .text("Категории товаров")
                .padding(0d, 0d, 10d, 0d);
        pie.legend()
                .position("bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        pie.legend().enabled(false);
        pie.title().enabled(false);
        pie.background("#393A40");
        anyChartView.setChart(pie);
    }*/
    private void setData(List<SpendCategory> spendCategoryList) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < spendCategoryList.size(); i++) {
            entries.add(new PieEntry((float) spendCategoryList.get(i).spend,
                    spendCategoryList.get(i).title));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(10f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());*/
        colors.add(Color.BLUE);
        colors.add(R.color.fabColor);
        colors.add(Color.WHITE);

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }


    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                v.setY(event.getRawY() + dY);
                v.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
                break;

            default:
                return false;
        }
        return true;
    }
}