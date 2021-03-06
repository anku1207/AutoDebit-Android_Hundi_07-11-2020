package com.uav.autodebit.exceptions;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.uav.autodebit.BO.ExceptionsBO;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AppErrorVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ExceptionsNotification {


    public static void ExceptionHandling(Context context, String e ,String... dataArry){

        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String showToast= (dataArry.length==0 ?"1":"0");//(leftButton ==null?"Modify": leftButton);
                    if(showToast.equals("1"))Toast.makeText(context, Content_Message.error_message, Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    ConnectionVO connectionVO = ExceptionsBO.sendErrorOnServer();

                    AppErrorVO appErrorVO =new AppErrorVO();
                    appErrorVO.setCustomerId(Session.getCustomerIdOnExceptionTime(context));
                    appErrorVO.setMobileName(Build.BRAND);
                    appErrorVO.setAppVersion(Utility.getVersioncode(context).toString());
                    appErrorVO.setErrorMessage(e);
                    appErrorVO.setMobileVersion(String.valueOf(Build.VERSION.SDK_INT));
                    appErrorVO.setDeviceInfo(Utility.getDeviceDetail().toString());

                    Gson gson =new Gson();
                    String json = gson.toJson(appErrorVO);
                    Log.w("request",json);
                    params.put("volley", json);
                    connectionVO.setParams(params);

                    VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                        }
                        @Override
                        public void onResponse(Object resp) throws JSONException {
                            JSONObject response = (JSONObject) resp;
                        }
                    });
                }
            });
        }catch (Exception e1){
            Log.w("Error",e1.getMessage());
        }
    }
}
