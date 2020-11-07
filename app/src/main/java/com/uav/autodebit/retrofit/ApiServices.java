package com.uav.autodebit.retrofit;

import com.uav.autodebit.vo.CustomerVO;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServices {

    @POST("{vc}/getMandateServiceListByCustomerIdOrServiceTypeId")
    Call<CustomerVO> getMandateServiceListByCustomerIdOrServiceTypeId(@Path("vc") String vc,@Header("versioncode") String versioncode, @Body RequestBody body);

}
