package com.example.seniorpj100per.History;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.seniorpj100per.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Detail_HistoryActivity extends AppCompatActivity {

    String img;
    String date;
    String namefood;
    String meal;
    String energy;

    Bitmap bitmap = null;

    ImageView imgView;
    TextView tv_date;
    TextView tv_namefood;
    TextView tv_meal;
    TextView tv_energy;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__history);

        imgView = findViewById(R.id.img_detail_history);
        tv_date = findViewById(R.id.tv_date_detail_history);
        tv_namefood = findViewById(R.id.tv_namefood_detail_history);
        tv_meal = findViewById(R.id.tv_meal_detail_history);
        tv_energy = findViewById(R.id.tv_energy_detail_history);
        toolbar = findViewById(R.id.toolbar_day_history);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("รายละเอียด");

        Intent intent = getIntent();
        img = intent.getStringExtra("img");
        date = intent.getStringExtra("date");
        namefood = intent.getStringExtra("namefood");
        meal = intent.getStringExtra("meal");
        energy = intent.getStringExtra("energy");
        setImgView(img);

        tv_date.setText(date);
        tv_namefood.setText(namefood);
        tv_meal.setText(meal);
        tv_energy.setText(energy+" kcals");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setImgView(String filename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("submit_food/" + filename);
        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

}
