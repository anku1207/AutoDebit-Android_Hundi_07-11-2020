package com.uav.autodebit.retrofit;

import com.uav.autodebit.constant.ApplicationConstant;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    static OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new HeaderIntercepter())
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApplicationConstant.getServerAddress())
            // .baseUrl("http://dummy.restapiexample.com/api/v1/")
            .client( okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static ApiServices getInterface() {
        return retrofit.create(ApiServices.class);
    }

    public static class  HeaderIntercepter implements Interceptor {

        public HeaderIntercepter(){}
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request =chain.request();
            request = request.newBuilder().addHeader("Content-Type","application/json")
                    .addHeader("authkey","G4s4cCMx2aM7lky1").build();
            request = request.newBuilder().build();

            return chain.proceed(request);
        }
    }

}
