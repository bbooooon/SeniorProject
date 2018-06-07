package com.example.seniorpj100per.History.Month;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.seniorpj100per.FoodList.Add_DataFoodlistToFireBase;
import com.example.seniorpj100per.History.Detail_HistoryActivity;
import com.example.seniorpj100per.Home.DataProvider;
import com.example.seniorpj100per.Home.HomeResult;
import com.example.seniorpj100per.R;
import com.example.seniorpj100per.UserObject;
import com.example.seniorpj100per.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Smew on 3/2/2561.
 */

public class Month_HistoryFragment extends Fragment {

    TextView tv_sum;
    TextView tv_setdate_to;
    Button btn_send;
    ListView lv_history;
    EditText edt_year;

    private int mYear, mMonth, mDay;

    private DatabaseReference journalCloudEndPoint;
    private DatabaseReference mDatabase;
    private List<Add_DataFoodlistToFireBase> mJournalEntries;

    UserProfile user;
    String username;

    protected List<HomeResult> data = new ArrayList<>();
    ListViewAdapter_History_Month adapter;

    int range = 0;
    String _day_from[];
    String _day_to[];
    int i = 0;
    String _date[];

    Spinner spinner;
    String text;
    String _text = "";
    double sum = 0;

    List<String> img_list = new ArrayList<>();
    List<String> date_list = new ArrayList<>();
    List<String> namefood_list = new ArrayList<>();
    List<String> meal_list = new ArrayList<>();
    List<String> energy_list = new ArrayList<>();

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_month_history, container, false);

        user = UserObject.INSTANCE.getUser();
        username = user.getUsername();

        tv_sum = v.findViewById(R.id.tv_setsumenergy_month);
        btn_send = v.findViewById(R.id.btn_send_month);
        lv_history = v.findViewById(R.id.lv_history_month);
        edt_year = v.findViewById(R.id.edt_year);

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        edt_year.setText(""+year);

        spinner = (Spinner) v.findViewById(R.id.spinner_month);
        List<String> categories = new ArrayList<String>();
        categories.add("มกราคม");
        categories.add("กุมภาพันธ์");
        categories.add("มีนาคม");
        categories.add("เมษายน");
        categories.add("พฤษภาคม");
        categories.add("มิถุนายน");
        categories.add("กรกฎาคม");
        categories.add("สิงหาคม");
        categories.add("กันยายน");
        categories.add("ตุลาคม");
        categories.add("พฤศจิกายน");
        categories.add("ธันวาคม");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        btn_send.setOnClickListener((View v1) -> {

            img_list.clear();
            date_list.clear();
            namefood_list.clear();
            meal_list.clear();
            energy_list.clear();

            sum = 0;
            text = (String) spinner.getSelectedItem();
            if (text.equals("มกราคม")) {
                _text = "1";
            }
            if (text.equals("กุมภาพันธ์")) {
                _text = "2";
            }
            if (text.equals("มีนาคม")) {
                _text = "3";
            }
            if (text.equals("เมษายน")) {
                _text = "4";
            }
            if (text.equals("พฤษภาคม")) {
                _text = "5";
            }
            if (text.equals("มิถุนายน")) {
                _text = "6";
            }
            if (text.equals("กรกฎาคม")) {
                _text = "7";
            }
            if (text.equals("สิงหาคม")) {
                _text = "8";
            }
            if (text.equals("กันยายน")) {
                _text = "9";
            }
            if (text.equals("ตุลาคม")) {
                _text = "10";
            }
            if (text.equals("พฤศจิกายน")) {
                _text = "11";
            }
            if (text.equals("ธันวาคม")) {
                _text = "12";
            }

            data = DataProvider.INSTANCE.getData();
            adapter = new ListViewAdapter_History_Month(data);
            DataProvider.INSTANCE.getData().clear();
            List<String> str = new ArrayList<>();
            List<String> str2 = new ArrayList<>();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            journalCloudEndPoint = mDatabase.child("add_data").child(username);
            mJournalEntries = new ArrayList<>();
            journalCloudEndPoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                        mJournalEntries.clear();
//
                        lv_history.setAdapter(null);
                        Add_DataFoodlistToFireBase note = noteSnapshot.getValue(Add_DataFoodlistToFireBase.class);
                        mJournalEntries.add(note);

                        String _year[];
                        _year = note.getDate().split("-");

                        if (note.getMonth().equals(_text) && _year[0].equals(edt_year.getText().toString())) {
                            reloadData(note);
                            img_list.add(note.getFilename());
                            date_list.add(note.getDate());
                            namefood_list.add(note.getNamefood());
                            meal_list.add(note.getTime());
                            energy_list.add(note.getEnergy());
                        }

                        for (int i=0; i<energy_list.size(); i++)
                        {
                            if (energy_list.get(i).equals("x"))
                            {
                                energy_list.set(i,"0.0");
                            }
                        }

                        Double sum_energy = 0.0;
                        for (int i = 0; i < energy_list.size(); i++) {
                            sum_energy += Double.parseDouble(energy_list.get(i));
                        }
                        tv_sum.setText("จำนวนแคลอรี่รวม :" + " " + sum_energy + " kcals");

                    }
                    lv_history.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("error", databaseError.getMessage());
                }

            });
        });

        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), Detail_HistoryActivity.class);
                intent.putExtra("img", img_list.get(position));
                intent.putExtra("date", date_list.get(position));
                intent.putExtra("namefood", namefood_list.get(position));
                intent.putExtra("meal", meal_list.get(position));
                intent.putExtra("energy", energy_list.get(position));
                startActivity(intent);
            }
        });

        return v;
    }

    public void reloadData(Add_DataFoodlistToFireBase note) {
        DataProvider.INSTANCE.getData().add(new HomeResult(note.getNamefood(),
                note.getEnergy(),
                0.0, 0.0, 0.0, note.getTime(), note.getDate()));
    }

}
