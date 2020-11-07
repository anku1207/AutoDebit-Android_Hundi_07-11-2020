package com.uav.autodebit.usersservices.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.uav.autodebit.usersservices.repo.MandateRevokeServiceWiseRepository;
import com.uav.autodebit.vo.CustomerServiceOperatorVO;
import com.uav.autodebit.vo.CustomerVO;

import java.util.ArrayList;

public class MandateRevokeServiceWiseViewModel /*extends AndroidViewModel*/ {

/*


    MandateRevokeServiceWiseRepository mandateRevokeServiceWiseRepository;
    public MandateRevokeServiceWiseViewModel(@NonNull Application application) {
        super(application);
        mandateRevokeServiceWiseRepository=new MandateRevokeServiceWiseRepository();
    }

    public LiveData<CustomerVO> getUserMutableLiveData(int serviceOperatorID, String customerID) {
        return mandateRevokeServiceWiseRepository.getServiceOperators(serviceOperatorID,customerID,getApplication().getApplicationContext());
    }



    public LiveData<Boolean> getIsLoading(){
       LiveData<Boolean> isLoading=MandateRevokeServiceWiseRepository.getInstance().getIsLoading();
        return isLoading;
    }


    public LiveData<Boolean> setMandateRevoked(int cSOID) {
        return mandateRevokeServiceWiseRepository.getIsmandateRevoked(cSOID,getApplication().getApplicationContext());
    }*/
}
