package com.uav.autodebit.usersservices.repo;

import android.content.Context;

import com.google.gson.Gson;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerServiceOperatorVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MandateRevokeServiceWiseRepository {
    public  void getServiceOperators(int serviceoperatorID, String customerID, Context context , VolleyResponse volleyResponse){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.getServiceOperatorMandateList();
            CustomerVO request_data = new CustomerVO();
            request_data.setCustomerId(Integer.parseInt(customerID));
            request_data.setServiceId(serviceoperatorID);
            Gson gson = new Gson();
            String json = gson.toJson(request_data);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }

                @Override
                public void onResponse(Object response) throws JSONException {
                    JSONObject resp = (JSONObject) response;
                    Gson gson = new Gson();
                    CustomerVO customerVO = gson.fromJson(resp.toString(), CustomerVO.class);
                    if(customerVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,customerVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(customerVO);
                    }
                }

            });
        }
        catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }

    public  void  getIsmandateRevoked(int cSoID,Context context , VolleyResponse volleyResponse){
        try {
            CustomerServiceOperatorVO customerServiceOperatorVO = new CustomerServiceOperatorVO();
            customerServiceOperatorVO.setcSOId(cSoID);
            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));
            customerServiceOperatorVO.setCustomer(customerVO);

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.updateMandateRevoked();
            String json = new Gson().toJson(customerServiceOperatorVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {

                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(Object response) throws JSONException {
                    JSONObject resp = (JSONObject) response;
                    Gson gson = new Gson();
                    CustomerVO customerVO = gson.fromJson(resp.toString(), CustomerVO.class);

                    if (customerVO.getStatusCode().equals("400")) {
                        ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < error.size(); i++) {
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context, customerVO.getDialogTitle(), sb.toString(), false);
                    } else {
                        volleyResponse.onSuccess(customerVO);
                    }

                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }
    public void getMandateSwapingType(Context context,int cSOId,int serviceTypeId,int customerId,VolleyResponse volleyResponse){

        try {
            CustomerServiceOperatorVO customerServiceOperatorVO = new CustomerServiceOperatorVO();
            customerServiceOperatorVO.setcSOId(cSOId);
            ServiceTypeVO serviceTypeVO = new ServiceTypeVO();
            serviceTypeVO.setServiceTypeId(serviceTypeId);
            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(customerId);
            customerServiceOperatorVO.setCustomer(customerVO);
            customerServiceOperatorVO.setServiceType(serviceTypeVO);

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.getMandateSwapingType();
            String json = new Gson().toJson(customerServiceOperatorVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {

                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(Object response) throws JSONException {
                    JSONObject resp = (JSONObject) response;
                    Gson gson = new Gson();
                    CustomerServiceOperatorVO respObject = gson.fromJson(resp.toString(), CustomerServiceOperatorVO.class);

                    if (respObject.getStatusCode().equals("400")) {
                        ArrayList error = (ArrayList) respObject.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < error.size(); i++) {
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context, respObject.getDialogTitle(), sb.toString(), false);
                    } else {
                        volleyResponse.onSuccess(respObject);
                    }

                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }

    public void getMandateDetails(Context context,int csoid,VolleyResponse volleyResponse) {
        try {
            CustomerServiceOperatorVO customerServiceOperatorVO = new CustomerServiceOperatorVO();
            customerServiceOperatorVO.setcSOId(csoid);
            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));
            customerServiceOperatorVO.setCustomer(customerVO);

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.getServiceMandateDetail();
            String json = new Gson().toJson(customerServiceOperatorVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                }

                @Override
                public void onResponse(Object response) throws JSONException {
                    JSONObject resp = (JSONObject) response;
                    Gson gson = new Gson();
                    CustomerServiceOperatorVO object = gson.fromJson(resp.toString(), CustomerServiceOperatorVO.class);
                    if (object.getStatusCode().equals("400")) {
                        ArrayList error = (ArrayList) object.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < error.size(); i++) {
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context, object.getDialogTitle(), sb.toString(), false);
                    } else {
                        volleyResponse.onSuccess(object);
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }



    public void saveMandateSwaping(Context context,int mandateId,int csoId,int providerId,int customerId,int serviceId,boolean mandatetype,VolleyResponse volleyResponse){

        try {
            ConnectionVO connectionVO = CustomerBO.saveMandateSwaping();
            CustomerServiceOperatorVO customerServiceOperatorVO = new CustomerServiceOperatorVO();
            customerServiceOperatorVO.setcSOId(csoId);
            customerServiceOperatorVO.setAnonymousInteger1(providerId);
            customerServiceOperatorVO.setAnonymousInteger(mandateId);

            CustomerVO  customerVO =new CustomerVO();
            customerVO.setCustomerId(customerId);
            customerServiceOperatorVO.setCustomer(customerVO);

            ServiceTypeVO serviceTypeVO = new ServiceTypeVO();
            serviceTypeVO.setServiceTypeId(serviceId);
            customerServiceOperatorVO.setServiceType(serviceTypeVO);
            customerServiceOperatorVO.setEventIs(mandatetype);
            HashMap<String, Object> params = new HashMap<String, Object>();
            String json = new Gson().toJson(customerServiceOperatorVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {

                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(Object response) throws JSONException {
                    JSONObject resp = (JSONObject) response;
                    Gson gson = new Gson();
                    CustomerServiceOperatorVO respObject = gson.fromJson(resp.toString(), CustomerServiceOperatorVO.class);

                    if (respObject.getStatusCode().equals("400")) {
                        ArrayList error = (ArrayList) respObject.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < error.size(); i++) {
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context, respObject.getDialogTitle(), sb.toString(), false);
                    } else {
                        volleyResponse.onSuccess(respObject);
                    }

                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
}
