package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.uav.autodebit.R;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.BeneAccVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddBeneficiaryActivity extends AppCompatActivity {

    EditText fullName,accountNumber,confirmAC,beneficiaryPhone,ifscCode;
    Button add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beneficiary);

        fullName = findViewById(R.id.fullName);
        accountNumber = findViewById(R.id.accountNumber);
        confirmAC = findViewById(R.id.confirmAC);
        beneficiaryPhone = findViewById(R.id.beneficiaryPhone);
        ifscCode = findViewById(R.id.ifscCode);
        add =findViewById(R.id.addBeneficiary);

        confirmAC.setLongClickable(false);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullName.getText().toString().isEmpty()){
                    fullName.setError("Full Name cannot be left blank");
                    return;
                }
                if(fullName.length()<3) {
                    fullName.setError("Full Name should be more than 2 letters");
                    return;
                }

                if(accountNumber.getText().toString().isEmpty()){
                    accountNumber.setError("Account Number cannot be left blank");
                    return;
                }
                if(accountNumber.length()<7 || accountNumber.length()>18) {
                    accountNumber.setError("Account Number  should be of 7-18 digits");
                    return;
                }
                if(confirmAC.getText().toString().isEmpty()){
                    confirmAC.setError("Confirm Account Number cannot be left blank");
                    return;
                }
                String ac= accountNumber.getText().toString();
                String cac = confirmAC.getText().toString();

                if((accountNumber.getText().toString()).equalsIgnoreCase(cac) ){
                    confirmAC.setError("Your Account Number and Confirm Account Number should be same");
                    return;
                }

                if(beneficiaryPhone.getText().toString().isEmpty()){
                    beneficiaryPhone.setError("Mobile number cannot be left blank");
                    return;
                }
                if(beneficiaryPhone.length()!=10 ){
                    beneficiaryPhone.setError("Mobile number should be of 10 digits.");
                    return;
                }

                if(ifscCode.getText().toString().isEmpty()){
                    ifscCode.setError("IFSC Code cannot be left blank");
                    return;
                }
                if(ifscCode.length()!=11) {
                    ifscCode.setError("IFSC Code should be of 11 digits");
                    return;
                }
                addBeneficiary();

                }
        });



    }

    private void addBeneficiary() {

        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("accountVerification");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);

        BeneAccVO beneAccVO =new BeneAccVO();
        beneAccVO.setBeneficialFullName(fullName.getText().toString().trim());
        beneAccVO.setBeneficialAccountNum(confirmAC.getText().toString().trim());
        beneAccVO.setBeneficialPhone(beneficiaryPhone.getText().toString().trim());
        beneAccVO.setBeneficialIFSCcode(beneficiaryPhone.getText().toString().trim());
        CustomerVO customerVO = new Gson().fromJson(Session.getSessionByKey(AddBeneficiaryActivity.this,Session.CACHE_CUSTOMER), CustomerVO.class);
        beneAccVO.setCustomer(customerVO);

        String json = new Gson().toJson(beneAccVO);

        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {

            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(Object response) throws JSONException {

                JSONObject jresponse = (JSONObject) response;
                Gson gson = new Gson();
                BaseVO baseVO = gson.fromJson(jresponse.toString(), BaseVO.class);

                if(baseVO.getStatusCode().equals("400")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    ArrayList error = (ArrayList) baseVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    // Utility.alertDialog(PanVerification.this,"Alert",sb.toString(),"Ok");
                    Utility.showSingleButtonDialog(AddBeneficiaryActivity.this,baseVO.getDialogTitle(),sb.toString(),false);
                }else {
                    if(baseVO.getStatusCode().equals("200")){
                        BeneAccVO beneAccVO1 =(BeneAccVO)response;
                        Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                            @Override
                            public void confirm(Dialog dialog) {

                            }

                            @Override
                            public void modify(Dialog dialog) {

                            }
                        }, AddBeneficiaryActivity.this,beneAccVO1.getBeneficialFullName()+" with "  +beneAccVO1.getBeneficialAccountNum()+" is added. \n Do you want to confirm?","", "Yes","No");



                    }
                }

            }
        });




        }

    public void onClickBackButton(View view) {
        finish();
    }
}