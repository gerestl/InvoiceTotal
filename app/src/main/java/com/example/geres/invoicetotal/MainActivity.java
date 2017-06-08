package com.example.geres.invoicetotal;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
        implements OnEditorActionListener, OnClickListener {

    private static final String TAG = "mainActivity";

    //define the varibles for the widget
    private EditText subtotalET;
    private TextView discountPercentTV;
    private TextView discountAmountTV;
    private TextView totalTV;
    private Button resetButton;

    //define instance variable
    private String subtotalString = "";

    private SharedPreferences savedValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get reference to the widgets
        subtotalET = (EditText) findViewById(R.id.subtotalET);
        discountPercentTV = (TextView) findViewById(R.id.discountPercentTV);
        discountAmountTV = (TextView) findViewById(R.id.discountAmountTV);
        totalTV = (TextView) findViewById(R.id.totalTV);
        resetButton = (Button) findViewById(R.id.resetButton);

        //set the listener for the event
        subtotalET.setOnEditorActionListener(this);
        resetButton.setOnClickListener(this);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.resetButton:
                subtotalET.setText("");
                discountPercentTV.setText("00%");
                discountAmountTV.setText("$0.00");
                totalTV.setText("$0.00");

                break;

        }

    }

    @Override
    public boolean onEditorAction(TextView textView,
                                  int actionId, KeyEvent keyEvent) {
       if(actionId == EditorInfo.IME_ACTION_DONE ||
               actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
           calculateAndDisplay();
       }

       Toast.makeText(getApplicationContext(), "Action ID: "+ actionId, Toast.LENGTH_LONG).show();
        //hide soft keyboard
        return false;
    }

    private void calculateAndDisplay() {

        //get subtotal from user
        subtotalString = subtotalET.getText().toString();
        float subtotal;
        if(subtotalString.equals("")){
            subtotal = 0;
        }
        else{
            subtotal = Float.parseFloat(subtotalString);
        }

        Log.d(TAG, "Subtotal: "+ subtotal);

        //get discount percent
        float discountPercent = 0;
        if(subtotal >= 200){
            discountPercent = .2f;
        }
        else if(subtotal >= 100){
            discountPercent = .1f;
        }
        else {
            discountPercent = 0;
        }

        //get discount amount
        float discountAmount = subtotal * discountPercent;
        float total = subtotal - discountAmount;

        //format and display
        NumberFormat percent = NumberFormat.getPercentInstance();
        discountPercentTV.setText(percent.format(discountPercent));

        NumberFormat currency = NumberFormat.getCurrencyInstance();
        discountAmountTV.setText(currency.format(discountAmount));
        totalTV.setText(currency.format(total));
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("subtotalString", subtotalString);
        editor.commit();



        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        subtotalString = savedValues.getString("subtotalString", "");
        subtotalET.setText(subtotalString);
        calculateAndDisplay();
    }
}
