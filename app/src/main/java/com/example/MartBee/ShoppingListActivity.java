package com.example.MartBee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    Button saveBtn, closeBtn;
    Fragment listFragment;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

//        Loading loading = new Loading(getApplicationContext());

        saveBtn = findViewById(R.id.saveBtn);
        closeBtn = findViewById(R.id.listCloseBtn);
        saveBtn = findViewById(R.id.saveBtn);
        closeBtn = findViewById(R.id.listCloseBtn);
        listFragment = new ListFragment();
        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name"); // 마트명


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(ShoppingListActivity.this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String floor, String startPoint, String mode) {
                        Toast.makeText(getApplicationContext(), floor, Toast.LENGTH_SHORT).show();

                        Intent toMapIntent = new Intent(ShoppingListActivity.this, MapActivity.class);
                        toMapIntent.putExtra("name", name); // 마트명
                        toMapIntent.putExtra("floor", floor); // n층
                        toMapIntent.putExtra("startPoint", startPoint); // 시작 지점
                        toMapIntent.putExtra("mode", mode); // pin or 최적 경로

//                        loading.init();
                        startActivity(toMapIntent);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                customDialog.setCanceledOnTouchOutside(true);
                customDialog.setCancelable(true);
//                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                WindowManager.LayoutParams params = customDialog.getWindow().getAttributes();
//                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                customDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);

                customDialog.show();
                Window window = customDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            }
        });
    }
}