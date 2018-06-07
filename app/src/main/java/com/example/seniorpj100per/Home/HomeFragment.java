package com.example.seniorpj100per.Home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.seniorpj100per.Cam.CameraActivity;
import com.example.seniorpj100per.Cam.GalleryActivity;
import com.example.seniorpj100per.FoodList.Add_DataFoodlistToFireBase;
import com.example.seniorpj100per.FoodList.FoodListFragment;
import com.example.seniorpj100per.History.Detail_HistoryActivity;
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
 * Created by Smew on 28/1/2561.
 */

public class HomeFragment extends Fragment {

    ListViewAdapter_Home_Today adapter_home_today;

    ListView lv_today;
    TextView tv_sumEnergy_today;
    private int mYear, mMonth, mDay;
    private Context mContext;

    protected List<HomeResult> data = new ArrayList<>();
    private Button btn_changedate_from;
    private Button btn_send;
    private TextView tv_date_from;
    private TextView tv_date_to;
    private String addType;

    private DatabaseReference journalCloudEndPoint;
    private DatabaseReference mDatabase;
    private List<Add_DataFoodlistToFireBase> mJournalEntries;

    String username = "";
    private ArrayList foodname;
    private ArrayList energy;
    private ArrayList date_;

    UserProfile user;
    List<String> img_list = new ArrayList<>();
    List<String> date_list = new ArrayList<>();
    List<String> namefood_list = new ArrayList<>();
    List<String> meal_list = new ArrayList<>();
    List<String> energy_list = new ArrayList<>();

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);
        mContext = getContext();

        lv_today = (ListView) v.findViewById(R.id.lv_today);
        tv_sumEnergy_today = (TextView) v.findViewById(R.id.tv_sumEnergy_today);
        tv_date_from = (TextView) v.findViewById(R.id.tv_date_from);

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int day = cc.get(Calendar.DAY_OF_MONTH);
        int _mouth = month + 1;
        tv_date_from.setText(year + "-" + _mouth + "-" + day);

        user = UserObject.INSTANCE.getUser();
        username = user.getUsername();

        foodname = new ArrayList<>();
        energy = new ArrayList<>();
        date_ = new ArrayList<>();

        img_list.clear();
        date_list.clear();
        namefood_list.clear();
        meal_list.clear();
        energy_list.clear();

        data = DataProvider.INSTANCE.getData();
        adapter_home_today = new ListViewAdapter_Home_Today(data);
        DataProvider.INSTANCE.getData().clear();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        journalCloudEndPoint = mDatabase.child("add_data").child(username);
        mJournalEntries = new ArrayList<>();

        journalCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    mJournalEntries.clear();

                    date_.clear();
                    foodname.clear();
                    energy.clear();
                    lv_today.setAdapter(null);
                    Add_DataFoodlistToFireBase note = noteSnapshot.getValue(Add_DataFoodlistToFireBase.class);
                    mJournalEntries.add(note);


                    date_.add(note.getDate());
                    foodname.add(note.getNamefood());
                    energy.add(note.getEnergy());

                    if (note.getDate().contains(tv_date_from.getText().toString())) {
                        reloadData(note);
                        img_list.add(note.getFilename());
                        date_list.add(note.getDate());
                        namefood_list.add(note.getNamefood());
                        meal_list.add(note.getTime());
                        energy_list.add(note.getEnergy());

                    }

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
                    tv_sumEnergy_today.setText("" + sum_energy);
                }

                lv_today.setAdapter(adapter_home_today);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error", databaseError.getMessage());
            }
        });

        lv_today.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_newdata_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actioc_add:

                final Dialog dialog2 = new Dialog(getContext());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_addnewdata);
                dialog2.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog2.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog2.show();
                dialog2.getWindow().setAttributes(lp);

                LinearLayout img_camera = (LinearLayout) dialog2.findViewById(R.id.img_camera_addnewdata);
                LinearLayout img_gallery = (LinearLayout) dialog2.findViewById(R.id.img_gallery_addnewdata);
                LinearLayout img_foodlist = (LinearLayout) dialog2.findViewById(R.id.img_foodlist_addnewdata);
                Button btn_cancel = (Button) dialog2.findViewById(R.id.btn_cancel_addnewdata);

                img_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("กล้องถ่ายรูป");
                        addType = "camera";
                        Intent intent = new Intent(getContext(), CameraActivity.class);
                        startActivity(intent);
                    }
                });

                img_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("คลังรูปภาพ");
                        addType = "gallery";
                        Intent intent = new Intent(getContext(), GalleryActivity.class);
                        startActivity(intent);
                    }
                });

                img_foodlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("คลังข้อมูลอาหาร");
                        Fragment fragment4 = new FoodListFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = getFragmentManager().beginTransaction();
                        fragmentTransaction4.replace(R.id.frame, fragment4);
                        fragmentTransaction4.commit();
                        dialog2.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reloadData(Add_DataFoodlistToFireBase note) {
        DataProvider.INSTANCE.getData().add(new HomeResult(note.getNamefood(),
                note.getEnergy(),
                0.0, 0.0, 0.0, "", ""));

    }

}

