package com.uav.autodebit.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.R;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.override.UAVEditText;

public class Bsnl_Landline_Corporate extends Base_Activity implements View.OnClickListener {
    EditText amount;
    ImageView back_activity_button;
    String operatorcode,operatorname=null;
    UAVEditText accountnumber;
    Button proceed;
    TextView fetchbill,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsnl__landline__corporate);

        getSupportActionBar().hide();

        amount=findViewById(R.id.amount);
        back_activity_button=findViewById(R.id.back_activity_button1);
        title=findViewById(R.id.title);
        proceed=findViewById(R.id.proceed);
        accountnumber=findViewById(R.id.accountnumber);
        fetchbill=findViewById(R.id.fetchbill);
        back_activity_button.setOnClickListener(this);
        fetchbill.setOnClickListener(this);
        proceed.setOnClickListener(this);

        title.setText(getIntent().getStringExtra("key"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button1:
                finish();
                break;
            case R.id.proceed:
                validatefiled("proceed");
                break;
            case R.id.fetchbill:
                if( validatefiled("fetchbill")){
                    accountnumber.setError(null);
                    amount.setError(null);
                    Toast.makeText(this, "sdfsd", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean validatefiled(String type){
        boolean valid=true;

        accountnumber.setError(null);
        amount.setError(null);
        fetchbill.setVisibility(View.VISIBLE);

        if(accountnumber.getText().toString().equals("")){
            accountnumber.setError(ErrorMsg.Field_Required);
            valid=false;
        }

        if(type.equals("proceed")){
            if(amount.getText().toString().equals("")){
                amount.setError(ErrorMsg.Field_Required);
                valid=false;
            }
        }

        if(!accountnumber.getText().toString().equals("") && accountnumber.getText().toString().length()<10){
            accountnumber.setError("Please enter correct Account Number here");
            valid=false;
        }
       return valid;

    }
}