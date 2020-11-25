package com.uav.autodebit.Activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BeneAccVO;

import java.util.ArrayList;

public class CreditCardBill extends Base_Activity implements View.OnClickListener {

    ImageView back_activity_button;
    LinearLayout addcardlistlayout,layoutmainBanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_bill);
        if(getSupportActionBar()!=null)  getSupportActionBar().hide();

        back_activity_button=findViewById(R.id.back_activity_button);
        addcardlistlayout=findViewById(R.id.addcardlistlayout);
        layoutmainBanner=findViewById(R.id.layoutmainBanner);
        back_activity_button.setOnClickListener(this);


        getCreditCardList(CreditCardBill.this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{


            try {


                ArrayList<BeneAccVO> beneAccVOS= (ArrayList<BeneAccVO>) new Gson().fromJson("", new TypeToken<ArrayList<BeneAccVO>>() { }.getType());
                if(beneAccVOS!=null && beneAccVOS.size()>0){
                   /* showAddCardBtn();
                    getdata(beneAccVOS);*/
                }else{
                    Intent  intent = new Intent(CreditCardBill.this,Credit_Card_Webview.class);
                    intent.putExtra(Credit_Card_Webview.EXTRAS_TITLE,"Credit Card Bill Pay");
                    intent.putExtra(Credit_Card_Webview.EXTRAS_URL,"https://www.google.com/");
                    intent.putExtra(Credit_Card_Webview.EXTRAS_FAIL_URL,"fail");
                    intent.putExtra(Credit_Card_Webview.EXTRAS_SUCCESS_URL,"success");
                    intent.putExtra(Credit_Card_Webview.EXTRAS_TXN_ID,"txnid");
                    startActivity(new Intent(intent));
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
                ExceptionsNotification.ExceptionHandling(CreditCardBill.this, Utility.getStackTrace(e));
            }
        }));
    }

    @SuppressLint("ResourceType")
    public void showAddCardBtn(){
        TextView textView = Utility.getTextView(CreditCardBill.this,"Add Credit Card");
        textView.setPaintFlags(textView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_blue_color_black));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        textView.setTag("addBeneficiaryBtn");

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
        textView.setTypeface(typeface ,Typeface.BOLD);

        int childCount = layoutmainBanner.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = layoutmainBanner.getChildAt(i);
            if((v.getTag()!=null) && (v.getTag().equals("addBeneficiaryBtn"))){
                Utility.removeEle(v);
            }
        }
        layoutmainBanner.addView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.removeEle(textView);
            }
        });
    }

    private void getCreditCardList(Context context , VolleyResponse volleyResponse) {
        volleyResponse.onSuccess(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                finish();
                break;
        }
    }
}
