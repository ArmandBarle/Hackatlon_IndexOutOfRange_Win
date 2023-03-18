package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemeUtils;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class MainActivity extends AppCompatActivity {

    HashMap<String,Object> data;

    ConstraintLayout cl;
    ScrollView sv;
    LinearLayout sll;
    LinearLayout bll;

    LinearLayout ll;

    ImageView iv;


    private void getFirebaseData(String shop){
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(shop+"/discount");
        Log.d("TestTest",data.getClass().toString());
        Log.d("TestTest","?????????");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TestTest",snapshot.getValue().getClass().toString());
                data = (HashMap<String, Object>) snapshot.getValue();
                fillUpPage(shop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TestTest","Error");
            }


        });
    }

    private void fillUpPage(String shop){
        TextView shopText = new TextView(this);
        shopText.setPadding(50,50,0,50);
        shopText.setTextSize(30);
        String cShop = shop.toUpperCase();
        shopText.setText(cShop);
        shopText.setTextColor(Color.parseColor("#FFFFFF"));
        ll.addView(shopText);

        for (Map.Entry<String, Object> item:data.entrySet()) {
            Log.d("TestTest",item.toString());
            View card = getLayoutInflater().inflate(R.layout.card,null);
            TextView name = card.findViewById(R.id.name);
            TextView amount = card.findViewById(R.id.amount);
            TextView price = card.findViewById(R.id.price);
            TextView percent = card.findViewById(R.id.percent);
            ImageView image = card.findViewById(R.id.image);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                name.setAutoSizeTextTypeUniformWithConfiguration(
                        1, 17, 1, TypedValue.COMPLEX_UNIT_DIP);
            }

            HashMap<String,Object> itemData = (HashMap<String,Object>)item.getValue();
            name.setText(itemData.get("name").toString());
            amount.setText(itemData.get("amount").toString());
            price.setText(itemData.get("current").toString() + " lei");
            percent.setText(itemData.get("percent").toString());
            String imageSource = itemData.get("pic").toString();
            Glide.with(this).load(imageSource).placeholder(R.drawable.ic_launcher_background).into(image);
            ll.addView(card);
        }
        sv.removeAllViews();
        bll.removeAllViews();
        sv.addView(ll);
        TextView title = new TextView(this);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,32,0,0);
        title.setLayoutParams(lp);
        title.setGravity(Gravity.CENTER);
        title.setText("Promotion");
        title.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setTextSize(50);
        bll.addView(title);
        bll.addView(sv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ll = new LinearLayout(this);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,100,0,0);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(0,0,0,120);
        sv = new ScrollView(this);
        sv.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));

        data = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cl = (ConstraintLayout)findViewById(R.id.scroll);
        bll = (LinearLayout)findViewById(R.id.baseVertical);

        Log.d("TestTest","TestTest");
        getFirebaseData("kaufland");
        getFirebaseData("carrefour");
        Log.d("TestTest","TestTest2");
        Log.d("TestTest",data.toString());
    }
}