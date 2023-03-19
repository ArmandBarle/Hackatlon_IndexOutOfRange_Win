package com.example.slist;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class Calculate extends AppCompatActivity {

    HashMap<String,Object> data;

    public static String shopToShow;

    private void getFirebaseData(String shop,TextView price){
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(shop+"/items");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data = (HashMap<String, Object>) snapshot.getValue();
                calculate(price);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TestTest","Error");
            }


        });
    }

    private void calculate(TextView price){
        float total = 0;
        for (String listItem:ShoppingList.items) {
            for (Map.Entry<String, Object> item : data.entrySet()) {
                HashMap<String, Object> itemData = (HashMap<String, Object>) item.getValue();
                String cItemCategory = itemData.get("category").toString();
                if (cItemCategory.compareTo(listItem) == 0) {
                    String cItemPriceString = itemData.get("current").toString();
                    cItemPriceString = cItemPriceString.replace(',', '.');
                    Float cItemPriceFloat = Float.parseFloat(cItemPriceString);
                    total += cItemPriceFloat;
                }
            }
        }
        price.setText("" + total);
        price.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button viewCarrefour = (Button)findViewById(R.id.showCarrefour);
        Button viewPenny = (Button)findViewById(R.id.showPenny);


        TextView price1 = (TextView)findViewById(R.id.price1);
        getFirebaseData("carrefour",price1);
        TextView price2 = (TextView)findViewById(R.id.price2);
        getFirebaseData("penny",price2);

        viewCarrefour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopToShow = "Carrefour";
                startActivity(new Intent(Calculate.this, ShowList.class));
            }
        });
        viewPenny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopToShow = "Penny";
                startActivity(new Intent(Calculate.this, ShowList.class));
            }
        });
    }
}
