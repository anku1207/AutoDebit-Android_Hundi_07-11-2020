package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.Activity.Base_Activity;
import com.uav.autodebit.Activity.Dmrc_NewAndExist_Card_Dialog;
import com.uav.autodebit.BO.IRCTCBO;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.CardTypeVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerServicesVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Change_Default_Mandate_Dialog extends Base_Activity implements View.OnClickListener {
    Button proceed;
    TextView dialog_title;
    Gson gson;
    LinearLayout main_layout;
    RadioGroup radiogroup ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__default__mandate__dialog);

        main_layout=findViewById(R.id.main_layout);
        proceed=findViewById(R.id.proceed);
        dialog_title=findViewById(R.id.dialog_title);
        radiogroup =findViewById(R.id.radiogroup);

        gson=new Gson();

        try {
            dialog_title.setText(getIntent().getStringExtra("title"));
            JSONArray jsonArray =new JSONArray( getIntent().getStringExtra("data"));

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getBoolean("active") && jsonObject.has("authId")){
                    RadioButton rdbtn = new RadioButton(this);
                    String text = jsonObject.getString("key");
                    rdbtn.setId(jsonObject.getInt("authId"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        rdbtn.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        rdbtn.setText(Html.fromHtml(text));
                    }
                    rdbtn.setChecked(false);
                    rdbtn.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(15, 15, 15, 15);
                    rdbtn.setLayoutParams(params);
                    rdbtn.setPadding(10,0,0,0);
                   //
                    // rdbtn.setGravity(Gravity.TOP);
                    rdbtn.setTag(jsonArray.get(i));
                    rdbtn.setChecked(jsonObject.getBoolean("default"));
                    radiogroup.addView(rdbtn);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =getWindow().getAttributes();
        params.gravity = Gravity.CENTER_VERTICAL;
        getWindow().setAttributes(params);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);


        proceed.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(MotionEvent.ACTION_DOWN == ev.getAction())
        {
            Rect dialogBounds = new Rect();
            getWindow().getDecorView().getHitRect(dialogBounds);
            if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
                // You have clicked the grey area
                finish();
                return false; // stop activity closing
            }
        }

        // Touch events inside are fine.
        return super.onTouchEvent(ev);

    }

    @Override
    public void onClick(View v) {
        try {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageviewclickeffect);
            switch (v.getId()){
                case R.id.proceed:
                    int selectedId = radiogroup.getCheckedRadioButtonId();
                    if(selectedId!=-1){
                        // find the radiobutton by returned id
                        RadioButton radioButton = (RadioButton) findViewById(selectedId);
                        JSONObject  jsonObject= (JSONObject) radioButton.getTag();
                        proceed.startAnimation(animation);

                        changeDefaultMandate(Change_Default_Mandate_Dialog.this,jsonObject,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                            CustomerServicesVO customerServicesVO  = (CustomerServicesVO) s;
                            Intent intent =new Intent();
                            setResult(RESULT_OK,intent);
                            finish();

                        }));

                    }else {
                        Toast.makeText(this, "Please pick the type of card you want to buy ", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }

    }

    private void changeDefaultMandate(Context context, JSONObject jsonObject, VolleyResponse volleyResponse) {

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MandateBO.setDefaultMandate();

            CustomerServicesVO  customerServicesVO =new CustomerServicesVO();


            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
            customerServicesVO.setCustomer(customerVO);

            CustomerAuthServiceVO customerAuthServiceVO  = new CustomerAuthServiceVO();
            customerAuthServiceVO.setCustomerAuthId(jsonObject.getInt("authId"));
            customerServicesVO.setDefaultAuth(customerAuthServiceVO);
            customerServicesVO.setCsId(jsonObject.getInt("csId"));

            Gson gson = new Gson();
            String json = gson.toJson(customerServicesVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("changeDefaultMandate",gson.toJson(customerServicesVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    CustomerServicesVO resp_CustomerServicesVO = gson.fromJson(response.toString(), CustomerServicesVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(resp_CustomerServicesVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) resp_CustomerServicesVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,"Error !",sb.toString(),false);
                        volleyResponse.onError(null);
                    }else {
                        volleyResponse.onSuccess(resp_CustomerServicesVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }


}