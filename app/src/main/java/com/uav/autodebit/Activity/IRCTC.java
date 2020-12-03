package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.animation.content.Content;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.IRCTCBO;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BeneAccVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerServicesVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IRCTC extends Base_Activity {
    ImageView imageview,back_activity_button;
    LinearLayout main_layout;
    int activeMandateCount;
    CustomerVO customerResponse;

    String customerServiceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irctc);
        getSupportActionBar().hide();

        imageview=findViewById(R.id.imageview);
        main_layout=findViewById(R.id.main_layout);
        back_activity_button=findViewById(R.id.back_activity_button);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getDetails();

    }
    @SuppressLint("ResourceType")
    public void getDetails(){
        getPaymentOpationForService(IRCTC.this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            try {
                customerResponse = (CustomerVO) success;
                JSONObject anonymousString1Data=new JSONObject(customerResponse.getAnonymousString1());
                Picasso.with(this).load(anonymousString1Data.getString("imageURL")).into(imageview, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //do smth when picture is loaded successfully
                    }
                    @Override
                    public void onError() {
                    }
                });
                Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
                TextView tv = new TextView(this);
                tv.setText(anonymousString1Data.getString("dialogTitle"));
                tv.setTypeface(typeface);
                tv.setLayoutParams(Utility.getLayoutparams(this,20,20,20,20));
                main_layout.addView(tv);


                JSONArray anonymousStringData=new JSONArray(customerResponse.getAnonymousString());
                RadioGroup radiogroup = new RadioGroup(this);
                for(int i=0;i<anonymousStringData.length();i++){
                    JSONObject jsonObject = anonymousStringData.getJSONObject(i);
                    if(jsonObject.getBoolean("active") && jsonObject.has("authId")){
                        tv = new TextView(this);
                        tv.setText(jsonObject.getString("bankName"));
                        tv.setTypeface(typeface);
                        tv.setLayoutParams(Utility.getLayoutparams(this,20,0,20,0));
                        tv.setTextColor(!jsonObject.getBoolean("default")?Utility.getColorWrapper(this,R.color.defaultTextColor):
                                Utility.getColorWrapper(this,R.color.green));
                        main_layout.addView(tv);
                        if(customerServiceId==null)customerServiceId= String.valueOf(jsonObject.getInt("csId"));
                        activeMandateCount +=1;
                    }else {
                        String text = jsonObject.getString("key");
                        RadioButton rdbtn = new RadioButton(this);
                        rdbtn.setId(jsonObject.getInt("value"));
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
                        rdbtn.setTag(jsonObject);
                        // rdbtn.setGravity(Gravity.TOP);
                        rdbtn.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                        radiogroup.addView(rdbtn);
                    }
                }
                main_layout.addView(radiogroup);
                Button proceed=Utility.getButton(this);
                proceed.setText("Proceed");
                proceed.setVisibility(View.GONE);
                main_layout.addView(proceed);

                if(anonymousString1Data.has("setDefaultMandateLink")){
                    tv = new TextView(this);
                    tv.setText(anonymousString1Data.getString("setDefaultMandateLink"));
                    tv.setTypeface(typeface);
                    tv.setLayoutParams(Utility.getLayoutparams(this,20,20,20,20));
                    tv.setTextColor(Utility.getColorWrapper(this,R.color.defaultTextColor));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_black));
                    //track text view set underline
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    main_layout.addView(tv);

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                startActivityForResult(new Intent(IRCTC.this, Change_Default_Mandate_Dialog.class).putExtra("data", anonymousStringData.toString()).putExtra("title",anonymousString1Data.getString("defaultMandatePopupTitle")),ApplicationConstant.REQ_DEFAULT_MANDATE_CHANGE);
                            }catch (Exception e ){
                                e.printStackTrace();
                                ExceptionsNotification.ExceptionHandling(IRCTC.this , Utility.getStackTrace(e));
                            }
                        }
                    });
                }

                if(anonymousString1Data.has("easyOTPExp") && anonymousString1Data.has("easyOTP") && anonymousString1Data.has("easyOTPMessage")){
                    tv = new TextView(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tv.setText(Html.fromHtml(anonymousString1Data.getString("easyOTPMessage"), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        tv.setText(Html.fromHtml(anonymousString1Data.getString("easyOTPMessage")));
                    }tv.setTypeface(typeface);
                    tv.setLayoutParams(Utility.getLayoutparams(this,20,0,20,0));
                    tv.setTextColor(Utility.getColorWrapper(this,R.color.defaultTextColor));
                    tv.setGravity(Gravity.CENTER);
                    //track text view set underline
                    //tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    main_layout.addView(tv);

                }else if(anonymousString1Data.has("OTP")){
                    JSONObject otpObject = anonymousString1Data.getJSONObject("OTP");

                    tv = new TextView(this);
                    tv.setText(otpObject.getString("link"));
                    tv.setTypeface(typeface);
                    tv.setLayoutParams(Utility.getLayoutparams(this,20,0,20,0));
                    tv.setTextColor(Utility.getColorWrapper(this,R.color.defaultTextColor));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_black));
                    //track text view set underline
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    main_layout.addView(tv);

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Utility.confirmationDialogTextType(new DialogInterface() {
                                    @Override
                                    public void confirm(Dialog dialog) {
                                        dialog.dismiss();

                                        if(customerServiceId!=null){
                                            generateOTP(IRCTC.this,customerServiceId,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                                                CustomerServicesVO customerServicesVO  = (CustomerServicesVO) s;
                                                main_layout.removeAllViews();
                                                getDetails();

                                            }));
                                        }else {
                                            Toast.makeText(IRCTC.this, Content_Message.error_message, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    @Override
                                    public void modify(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                },IRCTC.this,null,otpObject.getString("message"),otpObject.getString("popupTitle"),new String[]{"Yes","No"});
                            }catch (Exception e ){
                                e.printStackTrace();
                                ExceptionsNotification.ExceptionHandling(IRCTC.this , Utility.getStackTrace(e));
                            }
                        }
                    });
                }
                radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        proceed.setVisibility(View.VISIBLE);
                    }
                });

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            int selectedId = radiogroup.getCheckedRadioButtonId();
                            if(selectedId!=-1){
                                if(activeMandateCount>=1){
                                    JSONObject anonymousString1Data=new JSONObject(customerResponse.getAnonymousString1());
                                    JSONObject defaultMandateConcent = anonymousString1Data.getJSONObject("defaultMandateConcent");

                                    MyDialog.showCheckBoxSingleButtonDialog(IRCTC.this,defaultMandateConcent.getString("title"),defaultMandateConcent.getString("text"),new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
                                        int checkboxValue= (int) ok;
                                        Log.w("checkBox",checkboxValue+"");
                                        startActivityBySelectRadioButtonId(selectedId,checkboxValue);
                                    }));
                                }else {
                                    startActivityBySelectRadioButtonId(selectedId,1);
                                }
                            }else {
                                Toast.makeText(IRCTC.this, "Please select Mandate Type ", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            ExceptionsNotification.ExceptionHandling(IRCTC.this , Utility.getStackTrace(e));
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
            }
        }));
    }

    private void generateOTP(Context  context, String customerServiceId, VolleyResponse volleyResponse) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MandateBO.generateOTP();

            CustomerServicesVO customerServicesVO =new CustomerServicesVO();
            customerServicesVO.setCsId(Integer.parseInt(customerServiceId));

            Gson gson = new Gson();
            String json = gson.toJson(customerServicesVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("generateOTP",gson.toJson(customerServicesVO));
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

    public  void startActivityBySelectRadioButtonId(int radioBtnId ,int defaultMandate ){
        try {
            RadioButton radioButton = (RadioButton) findViewById(radioBtnId);
            JSONObject mandatePaymentObj = (JSONObject) radioButton.getTag();
            switch (radioBtnId){
                case 1:
                    BankMandate(IRCTC.this, ApplicationConstant.IRCTC,defaultMandate);
                    break;
                case 2:
                    startSIActivity(IRCTC.this,mandatePaymentObj.getInt("defaultMandateAmount"),ApplicationConstant.IRCTC,ApplicationConstant.PG_MANDATE,defaultMandate);
                    break;
                case 3:
                    startUPIActivity(IRCTC.this,mandatePaymentObj.getInt("defaultMandateAmount"),ApplicationConstant.IRCTC,ApplicationConstant.PG_MANDATE,defaultMandate);                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }

    }
    public  void BankMandate(Context context , int  serviceId ,int defaultMandate){
        startActivityForResult(new Intent(context,Enach_Mandate.class)
                .putExtra("forresutl",true)
                .putExtra("defaultMandate",defaultMandate)
                .putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))), ApplicationConstant.REQ_ENACH_MANDATE);
    }

    public static void startSIActivity(Context context , double amount , int serviceId,String paymentType,int defaultMandate){
        Intent intent = new Intent(context,SI_First_Data.class);
        intent.putExtra("amount",amount);
        intent.putExtra("serviceId",serviceId+"");
        intent.putExtra("paymentType",paymentType);
        intent.putExtra("defaultMandate",defaultMandate);
        ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_MANDATE);
    }

    public static void startUPIActivity(Context context  , double amount , int serviceId,String paymentType,int defaultMandate){
        Intent intent = new Intent(context,UPI_Mandate.class);
        intent.putExtra("amount",amount);
        intent.putExtra("serviceId",serviceId+"");
        intent.putExtra("paymentType",paymentType);
        intent.putExtra("defaultMandate",defaultMandate);
        ((Activity) context).startActivityForResult(intent, ApplicationConstant.REQ_UPI_FOR_MANDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(resultCode==RESULT_OK){
                 if(requestCode== ApplicationConstant.REQ_SI_MANDATE){
                    if(data !=null && data.getIntExtra("mandateId",0)!=0){
                        main_layout.removeAllViews();
                        getDetails();
                    }else {
                        Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                    }
                }else if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                     if(data !=null && data.getBooleanExtra("mandate_status",false)){
                         main_layout.removeAllViews();
                         getDetails();
                     }else {
                         Utility.showSingleButtonDialog(this,"Error !",data !=null?data.getStringExtra("msg"):Content_Message.error_message,false);
                     }
                 }else if(requestCode==ApplicationConstant.REQ_UPI_FOR_MANDATE){
                     if(data !=null && data.getIntExtra("mandateId",0)!=0){
                         main_layout.removeAllViews();
                         getDetails();
                     }else {
                         Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                     }
                 }else if(requestCode==ApplicationConstant.REQ_DEFAULT_MANDATE_CHANGE){
                     main_layout.removeAllViews();
                     getDetails();
                 }
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }


    private void getPaymentOpationForService(Context context, VolleyResponse volleyResponse) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.getPaymentOpationForService();
            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
            customerVO.setServiceId(ApplicationConstant.IRCTC);

            Gson gson = new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("OpationForService",gson.toJson(customerVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    CustomerVO resp_CustomerVO = gson.fromJson(response.toString(), CustomerVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(resp_CustomerVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) resp_CustomerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,"Error !",sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(resp_CustomerVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
}
