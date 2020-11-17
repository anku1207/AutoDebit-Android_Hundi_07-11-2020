package com.uav.autodebit.usersservices.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.Activity.Base_Activity;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.usersservices.repo.MandateRevokeServiceWiseRepository;
import com.uav.autodebit.usersservices.viewmodel.MandateRevokeServiceWiseViewModel;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerServiceOperatorVO;
import com.uav.autodebit.vo.CustomerVO;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MandateRevokeServiceWiseActivity extends Base_Activity {

    MandateRevokeServiceWiseViewModel viewModel ;
    RecyclerView recyclerView;
    TextView message;
    MandateRevokeServiceWiseRepository mandateRevokeServiceWiseRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_mandate_list);
        if(getSupportActionBar()!=null)
        getSupportActionBar().hide();

        mandateRevokeServiceWiseRepository=  new MandateRevokeServiceWiseRepository();
        message =findViewById(R.id.emptymsg);
        recyclerView = findViewById(R.id.rvActivatedServices);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        /*viewModel = ViewModelProviders.of(MandateRevokeServiceWiseActivity.this).get
                (MandateRevokeServiceWiseViewModel.class);*/
        getList();

    }

    public void  getList(){
        int serviceoperatorID = getIntent().getIntExtra("serviceID", 0);
        String customerID = Session.getCustomerId(MandateRevokeServiceWiseActivity.this);

        mandateRevokeServiceWiseRepository.getServiceOperators(serviceoperatorID,customerID,
                MandateRevokeServiceWiseActivity.this,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            CustomerVO customerVO = (CustomerVO) s;
            if (customerVO!=null) {
                try {
                    JSONArray jsonArray =new JSONArray(customerVO.getAnonymousString());
                    Type collectionType = new TypeToken<List<CustomerServiceOperatorVO>>() {}.getType();
                    ArrayList<CustomerServiceOperatorVO>customerServiceOperatorVOS =new Gson().fromJson(String.valueOf(jsonArray), collectionType);
                    if (customerServiceOperatorVOS.size()>0) {
                        message.setVisibility(View.GONE);
                        MandateRevokeServiceWiseListAdapter adapter = new MandateRevokeServiceWiseListAdapter(MandateRevokeServiceWiseActivity.this,
                                        customerServiceOperatorVOS);
                        recyclerView.setLayoutAnimation(Utility.getRunLayoutAnimation(MandateRevokeServiceWiseActivity.this));
                        recyclerView.scheduleLayoutAnimation();
                        recyclerView.setAdapter(adapter);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        if (recyclerView.getAdapter() != null)
                            recyclerView.getAdapter().notifyDataSetChanged();
                        recyclerView.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(MandateRevokeServiceWiseActivity.this , Utility.getStackTrace(e));
                }
            }
        }
      ));
    }
     public void onClickBackButton(View view) {
         Intent intent = new Intent();
         setResult(RESULT_OK,intent);
         finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode== ApplicationConstant.REQ_MANDATE_DETAIL_ACTIVITY_RESULT){
                getList();
                CustomerServiceOperatorVO customerServiceOperatorVO = (CustomerServiceOperatorVO) data.getSerializableExtra("objectResult");
                Utility.showSingleButtonDialog(this,customerServiceOperatorVO.getDialogTitle(),customerServiceOperatorVO.getAnonymousString(),false);
            }
        }
    }
}