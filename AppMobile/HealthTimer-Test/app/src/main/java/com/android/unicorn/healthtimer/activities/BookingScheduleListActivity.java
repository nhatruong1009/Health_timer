package com.android.unicorn.healthtimer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.unicorn.healthtimer.R;
import com.android.unicorn.healthtimer.fragments.BookingSearchListCustomAdapter;
import com.android.unicorn.healthtimer.fragments.BookingSearchListData;
import com.android.unicorn.healthtimer.fragments.BookingSearchListHospitalCustomAdapter;
import com.android.unicorn.healthtimer.fragments.BookingSearchListHospitalData;
import com.android.unicorn.healthtimer.viewmodels.BookingData;
import com.android.unicorn.healthtimer.viewmodels.HospitalService;
import com.android.unicorn.healthtimer.viewmodels.ListBooking;
import com.android.unicorn.healthtimer.viewmodels.ListHospital;
import com.android.unicorn.healthtimer.viewmodels.UserData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class BookingScheduleListActivity extends AppCompatActivity implements TextWatcher {

    private ListView list_booking;
    EditText schedule_search;
    private BookingSearchListCustomAdapter customAdapter;

    private ArrayList<BookingSearchListData> results;
    private ArrayList<BookingData> bookingData;
    private Button button_schedule_calendar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_schedule_list);

        UserData userData = UserData.getInstance();
        bookingData = new ArrayList<>();
        bookingData = userData.getBookingData();
        schedule_search = findViewById(R.id.activity_booking_schedule_list_search);
        schedule_search.addTextChangedListener(this);


        list_booking = findViewById(R.id.activity_booking_schedule_list_list_booking);

        if (bookingData.size() != 0){


            ArrayList<BookingSearchListData> temp = new ArrayList<>();
            for(int i = 0; i < bookingData.size(); i++){
                temp.add(new BookingSearchListData(bookingData.get(i).getDate(),bookingData.get(i).getTime(),bookingData.get(i).getServiceName(),bookingData.get(i).getHospitalName(),bookingData.get(i).getDone()));
            }

            Collections.sort(temp, new Comparator<BookingSearchListData>() {
                @Override
                public int compare(BookingSearchListData lhs, BookingSearchListData rhs) {
                    String tempdate1 = lhs.getDate();
                    String[] date1 = tempdate1.split("/");
                    String tempdate2 = rhs.getDate();
                    String[] date2 = tempdate2.split("/");

                    String temptime1 = lhs.getTime();
                    String[] time1 = temptime1.split(":");
                    int result1 = Integer.parseInt(time1[0]);
                    String temptime2 = lhs.getTime();
                    String[] time2 = temptime2.split(":");
                    int result2 = Integer.parseInt(time2[0]);

                    if (date1[2].compareTo(date2[2]) != 0){
                        return date1[2].compareTo(date2[2]);
                    }
                    if (date1[1].compareTo(date2[1]) != 0){
                        return date1[1].compareTo(date2[1]);
                    }
                    if (date1[0].compareTo(date2[0]) != 0){
                        return date1[0].compareTo(date2[0]);
                    }
                    if (result1 > result2){
                        return 1;
                    }
                    return -1;
                }
            });

            customAdapter = new BookingSearchListCustomAdapter(BookingScheduleListActivity.this, temp);
            list_booking.setAdapter(customAdapter);
        }
        else {
            TextView empty = findViewById(R.id.activity_booking_schedule_list_empty);
            empty.setText("Bạn chưa có lịch đặt khám bệnh nào");
        }

        button_schedule_calendar = findViewById(R.id.activity_booking_schedule_list_schedule_calendar);
        button_schedule_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookingScheduleCalendarActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (bookingData.size() != 0){
            UserData userData = UserData.getInstance();
            ArrayList<BookingData> temp = new ArrayList<>();
            temp = userData.getBookingData();

            results = new ArrayList<>();

            if (s!=null&&s.length()>0){
                s = s.toString().toUpperCase(Locale.ROOT);

                ArrayList<BookingSearchListData> filters = new ArrayList<>();
                for(int i = 0; i < temp.size(); i++){
                    String hospitalName = temp.get(i).getHospitalName();
                    String serviceName = temp.get(i).getServiceName();
                    String date = temp.get(i).getDate();
                    String time = temp.get(i).getTime();
                    if(hospitalName.toUpperCase(Locale.ROOT).contains(s) || serviceName.toUpperCase(Locale.ROOT).contains(s) || date.toUpperCase(Locale.ROOT).contains(s) || time.toUpperCase(Locale.ROOT).contains(s)){
                        filters.add(new BookingSearchListData(temp.get(i).getDate(),temp.get(i).getTime(),temp.get(i).getServiceName(),temp.get(i).getHospitalName(),temp.get(i).getDone()));
                    }
                }
                results.addAll(filters);
            }
            else {
                for(int i = 0; i < temp.size(); i++){
                    results.add(new BookingSearchListData(temp.get(i).getDate(),temp.get(i).getTime(),temp.get(i).getServiceName(),temp.get(i).getHospitalName(),temp.get(i).getDone()));
                }
            }

            Collections.sort(results, new Comparator<BookingSearchListData>() {
                @Override
                public int compare(BookingSearchListData lhs, BookingSearchListData rhs) {
                    String tempdate1 = lhs.getDate();
                    String[] date1 = tempdate1.split("/");
                    String tempdate2 = rhs.getDate();
                    String[] date2 = tempdate2.split("/");

                    String temptime1 = lhs.getTime();
                    String[] time1 = temptime1.split(":");
                    int result1 = Integer.parseInt(time1[0]);
                    String temptime2 = lhs.getTime();
                    String[] time2 = temptime2.split(":");
                    int result2 = Integer.parseInt(time2[0]);

                    if (date1[2].compareTo(date2[2]) != 0){
                        return date1[2].compareTo(date2[2]);
                    }
                    if (date1[1].compareTo(date2[1]) != 0){
                        return date1[1].compareTo(date2[1]);
                    }
                    if (date1[0].compareTo(date2[0]) != 0){
                        return date1[0].compareTo(date2[0]);
                    }
                    if (result1 > result2){
                        return 1;
                    }
                    return -1;
                }
            });
            customAdapter.setArrayList(results);
            list_booking.setAdapter(customAdapter);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
