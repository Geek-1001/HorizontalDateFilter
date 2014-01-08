package com.hornet.horizontalscrolldatepicker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HorizontalDateFilter datePickerWeek = (HorizontalDateFilter) findViewById(R.id.datePicker_week);
        datePickerWeek.setHorizontalDatePickerClickListener(new HorizontalDateFilter.HorizontalDateFilterClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id, Calendar currentDate) {
            }

            @Override
            public void onItemClickWeek(AdapterView<?> parent, View view, int position, long id, Calendar weekDateStart, Calendar weekDateEnd) {
                Toast.makeText(getApplicationContext(), "Week start day = " + weekDateStart.get(Calendar.DAY_OF_YEAR) + " week start month = " + weekDateStart.get(Calendar.MONTH) + " | Week end day = " + weekDateEnd.get(Calendar.DAY_OF_YEAR) + " week end month = " + weekDateEnd.get(Calendar.MONTH), Toast.LENGTH_LONG).show();
            }
        });
    }

}