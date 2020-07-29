package com.example.moneymanager.view.bottom_menu;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.moneymanager.R;
import com.example.moneymanager.generic.DB;
import com.example.moneymanager.generic.Handlers;
import com.example.moneymanager.model.Category;
import com.example.moneymanager.view.AddFragment;
import com.example.moneymanager.view.SublimePickerFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements OnChartValueSelectedListener, View.OnTouchListener{

    private PieChart chart;
    private List<Category> categoryList = new ArrayList<>();
    private float dX;
    private float dY;
    private int lastAction;
    private Button addSpendCategory;
    private LinearLayout linearLayout;
    private boolean check = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        final View dragView = root.findViewById(R.id.fab);
        dragView.setOnTouchListener(this);
        addSpendCategory = root.findViewById(R.id.addSpendCategory);
        linearLayout = root.findViewById(R.id.linearLayout);

        Handlers.redrawSpendCategories = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Handlers.redraw_OK:
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_host, new MainFragment())
                                .commit();
                        break;
                }

                return false;
            }
        });

        Handlers.fabClick = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Handlers.click_OK:
                        Fragment fragment = new AddFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_host, fragment)
                                .commit();
                        break;
                }
                return false;
            }
        });

        try {
            categoryList = DB.GetSpendCategoriesByTime("202007", "202008");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).total > 0.0) {
                check = true;
                break;
            }
        }
        Log.d("MyLog", String.valueOf(categoryList.get(0).total));

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
        drawCircle(chart.getHoleRadius());

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
        chart.getLegend().setEnabled(false);

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);
        setData(categoryList);

        addSpendCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpendCategory.setVisibility(View.GONE);
                linearLayout.setBackgroundResource(R.drawable.rounded_layout);
                final View[] view = addCategory();
                linearLayout.addView(view[0]);
                LinearLayout layout1 = new LinearLayout(getContext());
                layout1.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 6));
                layout1.setPadding(0, 0, 8, 0);
                layout1.addView(view[2]);
                LinearLayout layout2 = new LinearLayout(getContext());
                layout2.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 4));
                layout2.setPadding(8, 0, 0, 0);
                layout2.addView(view[3]);
                LinearLayout layout = (LinearLayout) view[1];
                layout.addView(layout1);
                layout.addView(layout2);
                linearLayout.setPadding(16, 8, 16, 8);
                linearLayout.addView(layout);
                view[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linearLayout.setWeightSum(15);
                        linearLayout.setPadding(32, 0, 32, 0);
                        for (int i = 0; i < view.length; i++) {
                            view[i].setVisibility(View.GONE);;
                        }
                        addSpendCategory.setVisibility(View.VISIBLE);
                        addSpendCategory.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 15
                        ));
                        linearLayout.setBackgroundResource(R.color.colorBackground);
                    }
                });
                view[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText name = (EditText) view[0];
                        String title = name.getText().toString();
                        if (title.equals("")) {
                            Toast.makeText(getContext(), "Введите название категории",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            DB.AddCategory(title, "expenses");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        linearLayout.setWeightSum(15);
                        linearLayout.setPadding(32, 0, 32, 0);
                        for (int i = 0; i < view.length; i++) {
                            view[i].setVisibility(View.GONE);
                        }
                        addSpendCategory.setVisibility(View.VISIBLE);
                        addSpendCategory.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0, 15
                        ));
                        linearLayout.setBackgroundResource(R.color.colorBackground);
                        Handlers.redrawSpendCategories.sendEmptyMessage(Handlers.redraw_OK);
                    }
                });
            }
        });

        return root;
    }

    private void setData(List<Category> categoryList) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        if (check) {
            for (int i = 0; i < categoryList.size(); i++) {
                entries.add(new PieEntry((float) categoryList.get(i).total,
                        categoryList.get(i).title));
                chart.setDrawEntryLabels(true);
                chart.setUsePercentValues(true);
            }

        }
        else {
            chart.setDrawEntryLabels(false);
            chart.setUsePercentValues(false);
            entries.add(new PieEntry((float) 1.0, ""));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setDrawValues(false);

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
            colors.add(c);*/

        colors.add(ColorTemplate.getHoloBlue());
        colors.add(Color.BLUE);
        colors.add(R.color.fabColor);
        colors.add(Color.WHITE);

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        /*data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);*/
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
                    Handlers.fabClick.sendEmptyMessage(Handlers.click_OK);
                break;

            default:
                return false;
        }
        return true;
    }

    @SuppressLint("ResourceAsColor")
    public void drawCircle(float r) {
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        paint.setColor(R.color.colorBlack);
        paint.setStyle(Paint.Style.FILL);
        //canvas.drawCircle((float) (screenWidth / 2.0), (float) (screenHeight / 2.0), r, paint);
        canvas.drawCircle(0, 0, r, paint);
    }

    public View[] addCategory() {
        EditText name = new EditText(getContext());
        name.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 7));
        name.setHint("Введите название категории");
        name.setPadding(32, 8, 32, 8);

        Button add = new Button(getContext());
        add.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 6));
        add.setText("Добавить");
        add.setPadding(0, 0, 8, 0);
        add.setBackgroundResource(R.drawable.rounded_button_for_new_account);

        Button cancel = new Button(getContext());
        cancel.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 4));
        cancel.setText("Отмена");
        cancel.setBackgroundResource(R.drawable.rounded_button_for_new_account);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(10);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 8));
        linearLayout.setPadding(16, 4, 16, 4);
        return new View[] {name, linearLayout, add, cancel};
    }
}