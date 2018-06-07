package com.example.seniorpj100per.FoodList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.seniorpj100per.HamburgerActivity;
import com.example.seniorpj100per.Home.DataProvider;
import com.example.seniorpj100per.Home.HomeResult;
import com.example.seniorpj100per.NamefoodByUserActivity;
import com.example.seniorpj100per.R;
import com.example.seniorpj100per.UserObject;
import com.example.seniorpj100per.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Smew on 22/9/2560.
 */

public class ResultActivity extends AppCompatActivity {

    ImageView img;
    ImageView img_save;
    ImageView img_cancel;
    static TextView tv_namefood;
    static TextView tv_energy;
    TextView tv_protein;
    TextView tv_lipid;
    TextView tv_carbs;
    static TextView tv_date;
    static Spinner spinner_day;
    private int mYear, mMonth, mDay;
    private Context mContext;
    FragmentManager fm = getSupportFragmentManager();

    private DatabaseReference journalCloudEndPoint;
    private DatabaseReference mDatabase;
    private List<KcalTable1> mJournalEntries;
    private int position;
    private String username;
    UserProfile user;

    private TextView change_energy;
    private TextView change_pro;
    private TextView change_li;
    private TextView change_carb;
    private ArrayAdapter<CharSequence> adapter;

    static int _month = 0;
    String foodlist = "";
    static String filename;

    Spinner spinner_dialog;
    private static String key_db;
    ArrayList<String> foodname_list;

    List<Add_DataFoodlistToFireBase> sampleJournalEntries;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_detailfood);
        tv_namefood = (TextView) findViewById(R.id.tv_namefood_detailfood);

        change_energy = (TextView) findViewById(R.id.change_energy);
        change_pro = (TextView) findViewById(R.id.change_pro);
        change_li = (TextView) findViewById(R.id.change_li);
        change_carb = (TextView) findViewById(R.id.change_carb);

        img = (ImageView) findViewById(R.id.img_detailfood);
        img_save = (ImageView) findViewById(R.id.img_save);
        img_cancel = (ImageView) findViewById(R.id.img_cancel);
        tv_namefood = (TextView) findViewById(R.id.tv_namefood_detailfood);
        tv_energy = (TextView) findViewById(R.id.tv_energy);
        tv_protein = (TextView) findViewById(R.id.tv_protein);
        tv_lipid = (TextView) findViewById(R.id.tv_lipid);
        tv_carbs = (TextView) findViewById(R.id.tv_carbs);
        tv_date = (TextView) findViewById(R.id.tv_date);
        spinner_day = (Spinner) findViewById(R.id.spinner_day);

        user = UserObject.INSTANCE.getUser();
        username = user.getUsername();

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        foodlist = intent.getStringExtra("foodlist");
        filename = intent.getStringExtra("filename");
        downloadLocalFile(filename);

        foodname_list = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        journalCloudEndPoint = mDatabase.child("kcaltable1");
        mJournalEntries = new ArrayList<>();
        journalCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    KcalTable1 note = noteSnapshot.getValue(KcalTable1.class);
                    mJournalEntries.add(note);
                    foodname_list.add(note.getFoodname_th());
                }

                foodname_list.remove(0);

                tv_namefood.setText(mJournalEntries.get(position).getFoodname_th());
                tv_energy.setText(mJournalEntries.get(position).getEnergy().toString());
                tv_carbs.setText(mJournalEntries.get(position).getCarbo().toString());
                tv_lipid.setText(mJournalEntries.get(position).getLipid().toString());
                tv_protein.setText(mJournalEntries.get(position).getProtein().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error", databaseError.getMessage());
            }
        });


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
        spinner_day.setAdapter(adapter);
            img_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(ResultActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setCancelable(true);

                    Spinner spinner_dialog = (Spinner) dialog.findViewById(R.id.spinner_dialog);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            getBaseContext(), android.R.layout.simple_spinner_item, foodname_list);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_dialog.setAdapter(spinnerArrayAdapter);

                    Button btn_cancel_dialog = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
                    Button btn_ok_dialog = (Button) dialog.findViewById(R.id.btn_ok_dialog);
                    Button btn_re_camera = (Button) dialog.findViewById(R.id.btn_re_camera);
                    Button btn_namebyuser = (Button) dialog.findViewById(R.id.btn_gotonamefoodbyuser);

                    btn_namebyuser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(getBaseContext(), NamefoodByUserActivity.class);
                            intent1.putExtra("filename", filename);
                            startActivity(intent1);
                        }
                    });

                    btn_re_camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(getBaseContext(), HamburgerActivity.class);
                            startActivity(intent1);
                        }
                    });

                    btn_cancel_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    btn_ok_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(getBaseContext(), ResultActivity.class);
                            int position = 0;
                            position = spinner_dialog.getSelectedItemPosition();
                            intent1.putExtra("position", position + 1);
                            intent1.putExtra("filename", mJournalEntries.get(position + 1).getFoodname_th() + ".jpg");
                            startActivity(intent1);
                        }
                    });

                    dialog.show();

                }
            });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namefood = tv_namefood.getText().toString();
                Double energy = Double.parseDouble(tv_energy.getText().toString());
                Double carbo = Double.parseDouble(tv_carbs.getText().toString());
                Double lipid = Double.parseDouble(tv_lipid.getText().toString());
                Double protein = Double.parseDouble(tv_protein.getText().toString());
                DataProvider.INSTANCE.getData().add(new HomeResult(namefood, energy.toString(), carbo, lipid, protein, "", ""));

                mDatabase = FirebaseDatabase.getInstance().getReference();
                journalCloudEndPoint = mDatabase.child("add_data");
                sampleJournalEntries = getSampleJournalEntries();
                for (Add_DataFoodlistToFireBase journalEntry : sampleJournalEntries) {
                    String key2 = journalCloudEndPoint.push().getKey();
                    String key = username;

                    key_db = tv_date.getText().toString() + "_" + key2;
                    journalEntry.setKey(key_db);
                    journalCloudEndPoint.child(key).child(key_db).setValue(journalEntry);
                }
                Intent intent = new Intent(getBaseContext(), HamburgerActivity.class);
                startActivity(intent);
            }
        });

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int day = cc.get(Calendar.DAY_OF_MONTH);
        _month = month + 1;
        tv_date.setText(year + "-" + _month + "-" + day);

    }

    public static List<Add_DataFoodlistToFireBase> getSampleJournalEntries() {
        List<Add_DataFoodlistToFireBase> journalEnrties = new ArrayList<>();
        journalEnrties.clear();
        Add_DataFoodlistToFireBase journalEntry1 = new Add_DataFoodlistToFireBase();
        journalEntry1.setDate(tv_date.getText().toString());
        journalEntry1.setEnergy(tv_energy.getText().toString());
        journalEntry1.setNamefood(tv_namefood.getText().toString());
        journalEntry1.setTime(spinner_day.getSelectedItem().toString());
        String _m[];
        _m = tv_date.getText().toString().split("-");
        journalEntry1.setMonth("" + _m[1]);
        journalEntry1.setFilename(filename);
        journalEnrties.add(journalEntry1);
        return journalEnrties;
    }

    @SuppressLint("ValidFragment")
    public class AlertDialogResult extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm")
                    .setMessage("Are you sure to save this menu ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), HamburgerActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();
        }
    }

    private void downloadLocalFile(String filename) {
        storageReference.child("submit_food/" + filename).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase get image error", exception.getLocalizedMessage());
            }
        });
    }

}
