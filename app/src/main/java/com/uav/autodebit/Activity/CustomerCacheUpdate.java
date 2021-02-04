package com.uav.autodebit.Activity;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.SQlLite.DataBaseHelper;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.LocalCacheVO;

import java.util.List;

public class CustomerCacheUpdate {
    public static void updateCustomerCache(Context context , CustomerVO customerVO){
        //3-02-2021 Manoj Shakya
        if(Session.getCustomerIdOnExceptionTime(context)!=null && !Session.getCustomerIdOnExceptionTime(context).equals(customerVO.getCustomerId() != null ? customerVO.getCustomerId().toString() : null)){
            context.deleteDatabase(DataBaseHelper.DB_PATH+DataBaseHelper.DB_NAME);
        }
        Gson gson = new Gson();
        Session.set_Data_Sharedprefence(context,Session.CACHE_CUSTOMER,gson.toJson(customerVO));
        if(customerVO.getLocalCache()!=null){
            LocalCacheVO customerVOLocalCache =  gson.fromJson(customerVO.getLocalCache(), LocalCacheVO.class);
            LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(context, Session.LOCAL_CACHE), LocalCacheVO.class);

            List<BannerVO> bannerVOS =customerVOLocalCache.getBanners();
            if(bannerVOS!=null && bannerVOS.size()>0){
                localCacheVO.setBanners(bannerVOS);
            }
            if(customerVOLocalCache.getUitiyServiceHomePage()!=null){
                localCacheVO.setUitiyServiceHomePage(customerVOLocalCache.getUitiyServiceHomePage());
            }
            if(customerVOLocalCache.getServiceHomePage()!=null){
                localCacheVO.setServiceHomePage(customerVOLocalCache.getServiceHomePage());
            }
            Session.set_Data_Sharedprefence(context, Session.LOCAL_CACHE, gson.toJson(localCacheVO));
        }
    }

    public static void updateLocalCache(Context context , String localCache){
        Gson gson = new Gson();

        if(localCache!=null){
            LocalCacheVO customerVOLocalCache =  gson.fromJson(localCache, LocalCacheVO.class);
            LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(context, Session.LOCAL_CACHE), LocalCacheVO.class);

            List<BannerVO> bannerVOS =customerVOLocalCache.getBanners();
            if(bannerVOS!=null && bannerVOS.size()>0){
                localCacheVO.setBanners(bannerVOS);
            }
            if(customerVOLocalCache.getUitiyServiceHomePage()!=null){
                localCacheVO.setUitiyServiceHomePage(customerVOLocalCache.getUitiyServiceHomePage());
            }
            if(customerVOLocalCache.getServiceHomePage()!=null){
                localCacheVO.setServiceHomePage(customerVOLocalCache.getServiceHomePage());
            }
            Session.set_Data_Sharedprefence(context, Session.LOCAL_CACHE, gson.toJson(localCacheVO));
        }
    }
}
