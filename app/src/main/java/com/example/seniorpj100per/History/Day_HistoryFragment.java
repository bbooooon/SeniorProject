package com.example.seniorpj100per.History;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.seniorpj100per.FoodList.Add_DataFoodlistToFireBase;
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

public class Day_HistoryFragment extends Fragment {

    TextView tv_setdate;
    TextView tv_setsum;
    Button btn_send;
    ListView lv_history;

    private int mYear, mMonth, mDay;

    private DatabaseReference journalCloudEndPoint;
    private DatabaseReference mDatabase;
    private List<Add_DataFoodlistToFireBase> mJournalEntries;

    UserProfile user;
    String username;

    protected List<HomeResult> data = new ArrayList<>();
    ListViewAdapter_History_Day adapter;
    double sum = 0;
    List<String> img_list = new ArrayList<>();
    List<String> date_list = new ArrayList<>();
    List<String> namefood_list = new ArrayList<>();
    List<String> meal_list = new ArrayList<>();
    List<String> energy_list = new ArrayList<>();

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_history, container, false);

        user = UserObject.INSTANCE.getUser();
        username = user.getUsername();

        tv_setdate = v.findViewById(R.id.tv_setdate_day);
        tv_setsum = v.findViewById(R.id.tv_setsumenergy_day);
        btn_send = v.findViewById(R.id.btn_send_day);
        lv_history = v.findViewById(R.id.lv_history_day);

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int day = cc.get(Calendar.DAY_OF_MONTH);
        int _mouth = month + 1;
        tv_setdate.setText(year + "-" + _mouth + "-" + day);

        tv_setdate.setOnClickListener(v1 -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    (view, year1, monthOfYear, dayOfMonth) ->
                            tv_setdate.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        btn_send.setOnClickListener(v1 -> {
            img_list.clear();
            date_list.clear();
            namefood_list.clear();
            meal_list.clear();
            energy_list.clear();

            sum = 0;
            data = DataProvider.INSTANCE.getData();
            adapter = new ListViewAdapter_History_Day(data);
            DataProvider.INSTANCE.getData().clear();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            journalCloudEndPoint = mDatabase.child("add_data").child(username);
            mJournalEntries = new ArrayList<>();
            journalCloudEndPoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                        mJournalEntries.clear();

                        lv_history.setAdapter(null);
                        Add_DataFoodlistToFireBase note = noteSnapshot.getValue(Add_DataFoodlistToFireBase.class);
                        mJournalEntries.add(note);

                        if (note.getDate().contains(tv_setdate.getText().toString())) {
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
                        tv_setsum.setText("จำนวนแคลอรี่รวม :" + " " + sum_energy + " kcals");
                    }

                    lv_history.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
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
                0.0, 0.0, 0.0, note.getTime(), ""));
    }

}
