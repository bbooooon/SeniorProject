package com.example.seniorpj100per;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.seniorpj100per.FoodList.Add_DataFoodlistToFireBase;
import com.example.seniorpj100per.Home.DataProvider;
import com.example.seniorpj100per.Home.HomeResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.seniorpj100per.FoodList.ResultActivity.getSampleJournalEntries;

public class NamefoodByUserActivity extends AppCompatActivity {

    EditText input_namefood;
    Button btn_ok;
    Button btn_home;
    static TextView tv_date;
    static Spinner spinner_meal;

    static String namefood_edt;
    private String username;
    UserProfile user;
    private static String key_db;
    private int mYear, mMonth, mDay;
    private Context mContext;

    private ArrayAdapter<CharSequence> adapter;

    static String filename;
    int _month=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namefood_by_user);

        input_namefood = findViewById(R.id.edt_inputnamefood);
        btn_ok = findViewById(R.id.btn_gotohome_save);
        btn_home = findViewById(R.id.btn_home);
        tv_date = findViewById(R.id.tv_date_byuser);
        spinner_meal = findViewById(R.id.spinner_day_byuser);

        user = UserObject.INSTANCE.getUser();
        username = user.getUsername();

        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int day = cc.get(Calendar.DAY_OF_MONTH);
        _month = month + 1;
        tv_date.setText(year + "-" + _month + "-" + day);

        mContext = this;
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                Activity activity = (Activity) mContext;
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tv_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_meal.setAdapter(adapter);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HamburgerActivity.class);
                startActivity(intent);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namefood_edt = input_namefood.getText().toString();
                DataProvider.INSTANCE.getData().add(new HomeResult(namefood_edt, "0", 0.0, 0.0, 0.0
                        , "", ""));

                DatabaseReference journalCloudEndPoint_;
                DatabaseReference mDatabase_;

                mDatabase_ = FirebaseDatabase.getInstance().getReference();
                journalCloudEndPoint_ = mDatabase_.child("add_data");
                List<Add_DataFoodlistToFireBase> sampleJournalEntries = getSampleJournalEntries();
                for (Add_DataFoodlistToFireBase journalEntry : sampleJournalEntries) {
                    String key2 = journalCloudEndPoint_.push().getKey();
                    String key = username;
                    key_db = tv_date.getText().toString() + "_" + key2;
                    journalEntry.setKey(key_db);
                    journalCloudEndPoint_.child(key).child(key_db).setValue(journalEntry);
                }
                Intent intent = new Intent(getBaseContext(), HamburgerActivity.class);
                startActivity(intent);
            }
        });
    }

    public static List<Add_DataFoodlistToFireBase> getSampleJournalEntries() {
        List<Add_DataFoodlistToFireBase> journalEnrties = new ArrayList<>();
        Add_DataFoodlistToFireBase journalEntry1 = new Add_DataFoodlistToFireBase();
        journalEntry1.setNamefood(namefood_edt);
        journalEntry1.setDate(tv_date.getText().toString());
        journalEntry1.setTime(spinner_meal.getSelectedItem().toString());
        String _m[];
        _m = tv_date.getText().toString().split("-");
        journalEntry1.setMonth("" + _m[1]);
        journalEntry1.setFilename(filename);
        journalEntry1.setEnergy("x");
        journalEnrties.add(journalEntry1);
        return journalEnrties;
    }

}
