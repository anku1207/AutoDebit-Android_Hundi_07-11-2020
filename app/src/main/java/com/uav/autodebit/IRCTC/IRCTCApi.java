package com.uav.autodebit.IRCTC;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.uav.autodebit.Activity.IRCTC;
import com.uav.autodebit.Activity.IRCTC_Webview;
import com.uav.autodebit.BO.IRCTCBO;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerServicesVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.PreMandateRequestVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class IRCTCApi {
    public static void createIRCTCBankMandateRequest(Context context , int customerId,int serviceId , VolleyResponse volleyResponse){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.createIRCTCBankMandateRequest();
            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(customerId);
            customerVO.setServiceId(serviceId);
            Gson gson = new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("ListByPaymentOption",gson.toJson(customerVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    PreMandateRequestVO preMandateRequestVO = gson.fromJson(response.toString(), PreMandateRequestVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(preMandateRequestVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) preMandateRequestVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,preMandateRequestVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(preMandateRequestVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }



    public static void updateIRCTCBankMandateStatus(Context context ,int authId ,int preMandateId , VolleyResponse volleyResponse){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.updateIRCTCBankMandateStatus();
            PreMandateRequestVO preMandateRequestVO = new PreMandateRequestVO();
            preMandateRequestVO.setRequestId(preMandateId);

            CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
            customerAuthServiceVO.setCustomerAuthId(authId);
            preMandateRequestVO.setCustomerAuth(customerAuthServiceVO);
            Gson gson = new Gson();
            String json = gson.toJson(preMandateRequestVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("updateIRCTCBank",gson.toJson(preMandateRequestVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    PreMandateRequestVO preMandateRequestVO = gson.fromJson(response.toString(), PreMandateRequestVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(preMandateRequestVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) preMandateRequestVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,preMandateRequestVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(preMandateRequestVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }

    public static void  getMandateListByPaymentOption(Context context , int paymentTypeId , VolleyResponse volleyResponse){

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.getMandateListByPaymentOption();

            CustomerServicesVO customerServicesVO = new CustomerServicesVO();

            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));

            customerServicesVO.setCustomer(customerVO);
            customerServicesVO.setServiceId(ApplicationConstant.IRCTC);
            customerServicesVO.setAnonymousInteger(paymentTypeId);

            Gson gson = new Gson();
            String json = gson.toJson(customerServicesVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("ListByPaymentOption",gson.toJson(customerServicesVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    PreMandateRequestVO preMandateRequestVO = gson.fromJson(response.toString(), PreMandateRequestVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(preMandateRequestVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) preMandateRequestVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,preMandateRequestVO.getDialogTitle(),sb.toString(),false);
                        volleyResponse.onError(null);
                    }else {
                        volleyResponse.onSuccess(preMandateRequestVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }


    public static void updateIRCTCDefaultMandate(Context context,int preMandateId, VolleyResponse volleyResponse) {

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.updateIRCTCDefaultMandate();
            PreMandateRequestVO  preMandateRequestVO =new PreMandateRequestVO();
            preMandateRequestVO.setRequestId(preMandateId);

            Gson gson = new Gson();
            String json = gson.toJson(preMandateRequestVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("IRCTCDefaultMandate",gson.toJson(preMandateRequestVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    PreMandateRequestVO preMandateRequestVO = gson.fromJson(response.toString(), PreMandateRequestVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(preMandateRequestVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) preMandateRequestVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,preMandateRequestVO.getDialogTitle(),sb.toString(),false);
                        volleyResponse.onError(null);
                    }else {
                        volleyResponse.onSuccess(preMandateRequestVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
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

    public static void getIRCTCPGURLs(Context context, VolleyResponse volleyResponse) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.getIRCTCPGURLs();
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
                        Utility.showSingleButtonDialog(context,resp_CustomerVO.getDialogTitle(),sb.toString(),false);
                        volleyResponse.onError(null);
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
