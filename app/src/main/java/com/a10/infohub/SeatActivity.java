package com.a10.infohub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a10.infohub.ui.seattable.SeatTable;

public class SeatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);

        SeatTable seatTable = (SeatTable) findViewById(R.id.activity_seat_seat);

        seatTable.setData(10, 15);
    }
}
