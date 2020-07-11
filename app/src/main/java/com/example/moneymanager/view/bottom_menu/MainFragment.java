package com.example.moneymanager.view.bottom_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.moneymanager.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    AnyChartView anyChartView;

    String[] months = {"Июнь", "Июль", "Август"};
    int[] spends = {20000, 15000, 30000};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        anyChartView = root.findViewById(R.id.any_chart_view);
        setupPieChart();
        return root;
    }

    public void setupPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntryList = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            dataEntryList.add(new ValueDataEntry(months[i], spends[i]));
        }
        pie.data(dataEntryList);
        pie.labels().position("outside");
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Retail channels")
                .padding(0d, 0d, 10d, 0d);
        anyChartView.setChart(pie);
    }
}