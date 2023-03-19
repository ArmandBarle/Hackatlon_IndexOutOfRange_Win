package com.example.slist;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {
    Button add,calc;
    AlertDialog dialog;
    LinearLayout layout;
    public static ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        items=new ArrayList<>();
        add=(Button) findViewById(R.id.add);
        calc=(Button) findViewById(R.id.calc);

        layout=(LinearLayout)findViewById(R.id.container);

        buildDialog(items);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#006B41"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#006B41"));

            }
        });

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShoppingList.this, Calculate.class));
            }
        });
    }

    private void buildDialog(ArrayList<String> pItems){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.dialog, null);

        AutoCompleteTextView name=view.findViewById(R.id.nameEdit);
        String[] hints=getResources().getStringArray(R.array.items);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,hints);
        name.setAdapter(arrayAdapter);

        builder.setView(view);
        builder.setTitle("Enter item")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!name.getText().toString().isEmpty()) {
                            pItems.add(name.getText().toString());
                            addCard(name.getText().toString(),pItems);
                        }
                        name.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        name.setText("");
        TextView textView = new TextView(this);
        textView.setText("Enter Item");
        textView.setPadding(50, 50, 20, 30);
        textView.setTextSize(20F);
        textView.setTextColor(Color.parseColor("#006B41"));
        textView.setPaintFlags(textView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        dialog = builder.create();
        dialog.setCustomTitle(textView);
    }

    private void addCard(String name, ArrayList<String> pItems){
        View view = getLayoutInflater().inflate(R.layout.card, null);
        TextView nameView = view.findViewById(R.id.name);
        Button delete=view.findViewById(R.id.delete);
        nameView.setText(name);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 */
                layout.removeView(view);
                if(pItems.size()>0) {
                    for(int i=0; i<pItems.size(); i++){
                        if(pItems.get(i).compareTo(name)==0){
                            pItems.remove(i);
                        }
                    }
                }
            }
        });
        layout.addView(view);
    }
}
