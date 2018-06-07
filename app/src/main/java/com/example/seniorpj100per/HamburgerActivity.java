package com.example.seniorpj100per;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seniorpj100per.FoodList.FoodListFragment;
import com.example.seniorpj100per.Healthy.HealthyFragment;
import com.example.seniorpj100per.History.Main_History_Fragment;
import com.example.seniorpj100per.Home.HomeFragment;
import com.example.seniorpj100per.Signin_SignUp.Signup_SigninActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Smew on 28/1/2561.
 */

public class HamburgerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private int PICK_IMAGE_REQUEST = 5678;
    private Uri filePath = null;
    private ImageView img_profile;
    String path = "";
    String username = "";
    String filename = "";
    UserProfile user;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamburger);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv_username_header = (TextView) header.findViewById(R.id.tv_username_header);
        img_profile = (ImageView) header.findViewById(R.id.profile_image);

        user = UserObject.INSTANCE.getUser();
        username = user.getUsername();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("addregis");
        myRef.child(username).child("filename").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filename = (String) dataSnapshot.getValue();
                setImageViewFromFirebase(filename);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        Intent intent = getIntent();
        if (intent.getStringExtra("username") != null) {
            tv_username_header.setText(username);
        }

        if (username == null) {
            tv_username_header.setText("");
        } else {
            tv_username_header.setText(username);
        }

        img_profile.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent1, PICK_IMAGE_REQUEST);
        });

        if (savedInstanceState == null) {
            toolbar.setTitle("หน้าหลัก");
            Fragment fragment1 = new HomeFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame, fragment1);
            fragmentTransaction1.commit();
        }

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.tab_home:
                        toolbar.setTitle("หน้าหลัก");
                        Fragment fragment1 = new HomeFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.frame, fragment1);
                        fragmentTransaction1.commit();
                        return true;
                    case R.id.tab_foodList:
                        toolbar.setTitle("คลังข้อมูลอาหาร");
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);

                        Fragment fragment4 = new FoodListFragment();
                        fragment4.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction4.replace(R.id.frame, fragment4);
                        fragmentTransaction4.commit();
                        return true;
                    case R.id.tab_getdb_fb:
                        toolbar.setTitle("ดูข้อมูลย้อนหลัง");
                        Fragment fragment6 = new Main_History_Fragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction6 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction6.replace(R.id.frame, fragment6);
                        fragmentTransaction6.commit();
                        return true;
                    case R.id.tab_healthyTricks:
                        toolbar.setTitle("เคล็ดลับสุขภาพ");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, HealthyFragment.newInstance("http://rssfeeds.sanook.com/rss/feeds/sanook/health.index.xml/"))
                                .commit();
                        return true;
                    case R.id.tab_logOut:
                        Intent intent_logout = new Intent(getBaseContext(), Signup_SigninActivity.class);
                        startActivity(intent_logout);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
               super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                filePath = data.getData();
                String[] FILE = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(filePath,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                path = cursor.getString(columnIndex);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bt = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference imageRef = storageReference.child("user/" + username + ".jpg");
                UploadTask uploadTask = imageRef.putBytes(bt);

                img_profile.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), filePath));
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("addregis");
                myRef.child(username).child("filename").setValue(username + ".jpg");

            } catch (Exception e) {
                Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public Bundle getMyData() {
        Bundle hm = new Bundle();
        hm.putString("username", username);
        return hm;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("username", username);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        username = savedInstanceState.getString("username");
    }

    private void setImageViewFromFirebase(String filename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("user/" + filename);
        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img_profile.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}
