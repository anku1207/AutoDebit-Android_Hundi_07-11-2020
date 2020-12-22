package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.NotificationBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerNotificationVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Skip_Mandate_Payment_Notification extends AppCompatActivity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener {
    TextView notification_content,title;
    ImageView notification_image;
    Button close;
    Switch switch_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip__mandate__payment__notification);
        getSupportActionBar().hide();

        notification_content=findViewById(R.id.notification_content);
        title=findViewById(R.id.title);
        notification_image=findViewById(R.id.notification_image);
        close=findViewById(R.id.close);
        switch_btn=findViewById(R.id.switch_btn);
        close.setOnClickListener(this);
        switch_btn.setOnCheckedChangeListener(this);

        try {
            JSONObject jsonObject =new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));

            if(jsonObject.has("value") &&   jsonObject.isNull("value")){
                Utility.showSingleButtonDialog(this,"Error !", Content_Message.error_message,true);
            }else{
                getNotificationDetails(Skip_Mandate_Payment_Notification.this,jsonObject.getInt("value"),
                        new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                            CustomerNotificationVO customerNotificationVO = (CustomerNotificationVO) success;

                            close.setVisibility(View.VISIBLE);
                            switch_btn.setVisibility(View.VISIBLE);

                            title.setText(customerNotificationVO.getTitle());
                            if(customerNotificationVO.getBigImage()!=null){
                                Picasso.with(Skip_Mandate_Payment_Notification.this)
                                        .load(customerNotificationVO.getBigImage())
                                        .into(notification_image);
                            }
                            notification_content.setText(customerNotificationVO.getMessage());

                            switch_btn.setText(customerNotificationVO.getAnonymousString());

                        }));
            }
        }catch (Exception e){
            Utility.showSingleButtonDialog(this,"Error !", e.getMessage(),true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                finish();
                break;
        }

    }

    void getNotificationDetails(Context context, int notificationId, VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = NotificationBO.getNotificationByCNId();
        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));

        CustomerNotificationVO customerNotificationVO =new CustomerNotificationVO();
        customerNotificationVO.setCnId(notificationId);
        customerNotificationVO.setCustomer(customerVO);

        Gson gson =new Gson();
        String json = gson.toJson(customerNotificationVO);
        Log.w("request",json);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerNotificationVO responseVO = gson.fromJson(response.toString(), CustomerNotificationVO.class);

                if(responseVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) responseVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,responseVO.getDialogTitle(),sb.toString(),true);
                }else {
                    //update customer cache
                    volleyResponse.onSuccess(responseVO);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.switch_btn){
            if (isChecked) {
                Toast.makeText(Skip_Mandate_Payment_Notification.this, "true", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Skip_Mandate_Payment_Notification.this, "false", Toast.LENGTH_SHORT).show();
            }
        }
    }
}