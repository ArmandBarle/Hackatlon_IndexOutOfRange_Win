package com.example.slist;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ShowList extends AppCompatActivity {

    HashMap<String,Object> data;

    ConstraintLayout cl;
    ScrollView sv;
    LinearLayout sll;
    LinearLayout bll;

    LinearLayout ll;

    ImageView iv;

    HashSet<String> soloStrings;

    private void getFirebaseData(String shop){
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(shop+"/items");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

        for (String listItem:soloStrings) {
            for (Map.Entry<String, Object> item : data.entrySet()) {
                HashMap<String, Object> itemData = (HashMap<String, Object>) item.getValue();
                String cItemCategory = itemData.get("category").toString();
                if (cItemCategory.compareTo(listItem) == 0) {
                    View card = getLayoutInflater().inflate(R.layout.listcard, null);
                    TextView name = card.findViewById(R.id.name);
                    TextView amount = card.findViewById(R.id.amount);
                    TextView price = card.findViewById(R.id.price);
                    ImageView image = card.findViewById(R.id.image);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        name.setAutoSizeTextTypeUniformWithConfiguration(
                                1, 17, 1, TypedValue.COMPLEX_UNIT_DIP);
                    }

                    itemData = (HashMap<String, Object>) item.getValue();
                    name.setText(itemData.get("name").toString());
                    amount.setText(itemData.get("amount").toString());
                    price.setText(itemData.get("current").toString() + " lei");
                    String imageSource = itemData.get("pic").toString();
                    Glide.with(this).load(imageSource).placeholder(R.drawable.ic_launcher_background).into(image);
                    ll.addView(card);
                }
            }
        }
        sv.removeAllViews();
        bll.removeAllViews();
        sv.addView(ll);
        TextView title = new TextView(this);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,32,0,0);
        title.setLayoutParams(lp);
        title.setGravity(Gravity.CENTER);
        title.setText("Your Items From" + Calculate.shopToShow);
        title.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setTextSize(50);
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

        soloStrings = new HashSet<>();

        for (String item:ShoppingList.items) {
            soloStrings.add(item);
        }

        data = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showlist);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cl = (ConstraintLayout)findViewById(R.id.scroll);
        bll = (LinearLayout)findViewById(R.id.baseVertical);

        getFirebaseData(Calculate.shopToShow.toLowerCase(Locale.ROOT));
    }
}