package com.example.financy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.financy.Constants.*;

public class ActivityOverview extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private SharedPreferences mSharedPreferences;
    private List<String> mAmounts;
    private List<String> mTitles;
    private PieChart mPieChart;
    private ListView mListView;
    private String mIncome;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        setup();
        loadEntries();
        setupPieChart();
        loadPieChartData();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadEntries() {
        ArrayList<Entry> allEntries = new ArrayList<>();
        ArrayList<Entry> sortedEntries = new ArrayList<>();
        for (int i = 0; i < mTitles.size(); i++) {
            allEntries.add(new Entry(mTitles.get(i), Double.parseDouble(mAmounts.get(i))));
        }

        Collections.sort(allEntries, Comparator.comparing(Entry::getAmount));

        for (int i = allEntries.size()-1; i >= 0; i--) {
            sortedEntries.add(allEntries.get(i));
        }

        EntryListAdapter adapter = new EntryListAdapter(this, R.layout.listview_adapter_layout, sortedEntries);
        mListView.setAdapter(adapter);
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void loadAmounts() {
        mAmounts = new ArrayList<>();
        String tmp = mSharedPreferences.getString(SAVE_NAME_AMOUNTS, "");
        if (!tmp.equals("")) {
            for (String amount : tmp.split(SHARED_PREFERENCES_DELIMITER)) {
                mAmounts.add(amount);
            }
        }
    }

    private void loadTitles() {
        mTitles = new ArrayList<>();
        String tmp = mSharedPreferences.getString(SAVE_NAME_TITLES, "");
        if (!tmp.equals("")) {
            for (String title : tmp.split(SHARED_PREFERENCES_DELIMITER)) {
                mTitles.add(title);
            }
        }
    }

    private void loadIncome() {
        mIncome = mSharedPreferences.getString(SAVE_NAME_INCOME, "");
        if (mIncome.equals("")) {
            mIncome = DEFAULT_INCOME;
        }
    }

    private void setupPieChart() {
        mPieChart.setUsePercentValues(true);
        mPieChart.setEntryLabelTextSize(12);
        mPieChart.setEntryLabelColor(Color.BLACK);
        mPieChart.getDescription().setEnabled(false);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        Float expenses = 0f;
        for (int i = 0; i < mAmounts.size(); i++) {
            expenses = expenses + Float.parseFloat(mAmounts.get(i));
        }

        entries.add(new PieEntry(expenses, "Ausgaben"));
        entries.add(new PieEntry(Float.parseFloat(mIncome)-expenses, "Rest"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor(COLOR_EXPENSES));
        colors.add(Color.parseColor(COLOR_REST));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        mPieChart.setCenterText(String.valueOf(Float.parseFloat(mIncome) - expenses) + "€");
        mPieChart.setCenterTextSize(15);
        mPieChart.setData(data);
        mPieChart.invalidate();

        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void setup() {
        mSharedPreferences = getSharedPreferences(SAVE_NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPieChart = findViewById(R.id.pie_chart);
        mListView = findViewById(R.id.list_view);
        setupBottomNavigationBar();
        loadTitles();
        loadAmounts();
        loadIncome();
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.show_statistics);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.show_statistics:
                        return true;
                    case R.id.add_entry:
                        startActivity(new Intent(getApplicationContext(), ActivityAdd.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

}