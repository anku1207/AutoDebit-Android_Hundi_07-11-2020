
package com.uav.autodebit.DMRC;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.uav.autodebit.Activity.Dmrc_Card_Request;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.PinCodeBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DMRCApi {
    public static void pinCodebycity(Context context, String pincode, VolleyResponse volleyResponse){
        VolleyUtils.makeJsonObjectRequest(context, PinCodeBO.getCityByPincode(pincode), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CityVO cityVO = gson.fromJson(response.toString(), CityVO.class);
                if(cityVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) cityVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    volleyResponse.onError(sb.toString());
                }else {

                    volleyResponse.onSuccess(cityVO);
                }
            }
        });
    }

    public static void getCityByPincodeForDMRC(Context context, String pincode, VolleyResponse volleyResponse){
        VolleyUtils.makeJsonObjectRequest(context, PinCodeBO.getCityByPincodeForDMRC(pincode), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CityVO cityVO = gson.fromJson(response.toString(), CityVO.class);
                if(cityVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) cityVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    volleyResponse.onError(sb.toString());
                }else {

                    volleyResponse.onSuccess(cityVO);
                }
            }
        });

    }

    public static void dmrcCardCancelAndRefund(Context context,int dmrcId , int customerId,VolleyResponse volleyResponse ){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MetroBO.dmrcCardCancle();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(customerId);
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(dmrcId);

        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("request",json);
        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                DMRC_Customer_CardVO dmrc_customer_cardVO = new Gson().fromJson(response.toString(),DMRC_Customer_CardVO.class);
                if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    volleyResponse.onError(sb.toString());
                }else {
                    volleyResponse.onSuccess(dmrc_customer_cardVO);
                }
            }
        });



    }
}
