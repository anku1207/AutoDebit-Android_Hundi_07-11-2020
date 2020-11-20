package com.uav.autodebit.adpater;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.uav.autodebit.Activity.AddBeneficiaryActivity;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BeneAccVO;
import java.util.HashMap;
import java.util.List;

public class BeneficiaryPagerAdapter extends PagerAdapter {

    private List<BeneAccVO> models;
    private LayoutInflater layoutInflater;
    private Context context;


    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    private HashMap<Integer , Boolean> clickViewpager = new HashMap<Integer, Boolean>();


    public BeneficiaryPagerAdapter(List<BeneAccVO> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(container.getContext())
                .inflate(R.layout.design_beneficiary_card_list, container, false);
        LinearLayout main_container = itemView.findViewById(R.id.main_container);
        TextView beneficiary_desc=itemView.findViewById(R.id.beneficiary_desc);

        BeneAccVO accVO = models.get(position);

        String detail="Id : "+accVO.getBeneficialAccountId()+
                "\n"+"Name : "+accVO.getBeneNameRespose()+
                "\n"+"IFSC Code : "+accVO.getBeneficialIFSCcode()+
                "\n"+"Account No. : "+accVO.getBeneficialAccountNum();
        beneficiary_desc.setText(detail);
        main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.enableDisableView(v,false);
                ((AddBeneficiaryActivity)context).openPaymentDialog(accVO,v);
            }
        });
        container.addView(itemView);
        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
