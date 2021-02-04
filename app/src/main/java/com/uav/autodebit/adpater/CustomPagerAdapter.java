package com.uav.autodebit.adpater;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.uav.autodebit.Activity.Dmrc_Card_Request;
import com.uav.autodebit.Activity.Track_Dmrc_Card;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.DMRC.DMRCApi;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.RefundVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CustomPagerAdapter extends PagerAdapter {

    private List<DMRC_Customer_CardVO> models;
    private LayoutInflater layoutInflater;
    private Context context;


    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    private HashMap<Integer, Boolean> clickViewpager = new HashMap<Integer, Boolean>();


    public CustomPagerAdapter(List<DMRC_Customer_CardVO> models, Context context) {
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
                .inflate(R.layout.design_dmrc_card_list, container, false);

        LinearLayout mainlayout;
        TextView name, cardnumber, status, issuedate, fcardstatus, track;
        ImageView imageview;

        mainlayout = itemView.findViewById(R.id.mainlayout);
        name = itemView.findViewById(R.id.name);
        name.setText("  " + models.get(position).getCustomerName());
        cardnumber = itemView.findViewById(R.id.cardnumber);
        status = itemView.findViewById(R.id.status);
        issuedate = itemView.findViewById(R.id.issuedate);
        imageview = itemView.findViewById(R.id.imageview);

        fcardstatus = itemView.findViewById(R.id.fcardstatus);
        track = itemView.findViewById(R.id.track);

        //track text view set underline
        track.setPaintFlags(track.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        DMRC_Customer_CardVO pro = models.get(position);
        name.setText("  " + pro.getCustomerName());
        fcardstatus.setText("Status : " + pro.getDmrccardStaus().getStatusName());
        cardnumber.setText(pro.getCardNo());

        if (pro.getIssueDate() != null) {
            Date date = new Date(pro.getIssueDate());
            issuedate.setText("Issue Date \n" + new SimpleDateFormat("dd-MM-yyyy").format(date));
        } else {
            issuedate.setText("");
        }
        if (pro.getImage() != null) {
            Picasso.with(context)
                    .load(pro.getImage())
                    .fit()
                    .error(R.drawable.dmrc)
                    .placeholder(R.drawable.dmrc)
                    .into(imageview);
        } else {
            imageview.setImageURI(null);
        }

        clickViewpager.put(position, false);

        //if track is true then show track link in dmrc card (cancel /refund)
        if (!pro.isTrackCard() && !pro.isCancelAndRefund()) {
            track.setVisibility(View.GONE);
        } else if ((pro.isTrackCard() && !pro.isCancelAndRefund()) || (pro.isTrackCard() && pro.isCancelAndRefund())) {
            track.setVisibility(View.VISIBLE);
            track.setText("Track");
        } else if (!pro.isTrackCard() && pro.isCancelAndRefund()) {
            track.setVisibility(View.VISIBLE);
            track.setText("Cancel / Refund");
        }

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pro.getDmrcid() != null) {
                    if ((pro.isTrackCard() && !pro.isCancelAndRefund()) || (pro.isTrackCard() && pro.isCancelAndRefund())) {
                        ((Activity) context).startActivity(new Intent(context, Track_Dmrc_Card.class).putExtra("cardId", pro.getDmrcid() + ""));
                    } else if (!pro.isTrackCard() && pro.isCancelAndRefund()) {
                        Utility.confirmationDialogTextType(new DialogInterface() {
                            @Override
                            public void confirm(Dialog dialog) {
                                Utility.dismissDialog(context, dialog);
                                DMRCApi.dmrcCardCancelAndRefund(context, pro.getDmrcid(), Integer.parseInt(Session.getCustomerId(context)), new VolleyResponse((VolleyResponse.OnSuccess) (success) -> {
                                    DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) success;
                                    Activity activity = (Activity) context;
                                    if (dmrc_customer_cardVO.isEventIs()) {
                                        MyDialog.addBankDetails(context, "Refund Details :- ", false, new ConfirmationGetObjet((ConfirmationGetObjet.OnOk) (ok) -> {
                                            HashMap<String, Object> objectsHashMap = (HashMap<String, Object>) ok;
                                            Dialog dialog1 = (Dialog) objectsHashMap.get("dialog");
                                            Utility.dismissDialog(context, dialog1);

                                            RefundVO refundVO = (RefundVO) objectsHashMap.get("data");
                                            refundVO.setAnonymousInteger(pro.getDmrcid());
                                            DMRCApi.saveNFTDetailsForDmrc(context, refundVO, new VolleyResponse((VolleyResponse.OnSuccess) (successNft) -> {
                                                DMRC_Customer_CardVO successNft1 = (DMRC_Customer_CardVO) successNft;
                                                if (activity.getClass().getSimpleName().equals("Dmrc_Card_Request")) {
                                                    Utility.showSingleButtonDialog(context, successNft1.getDialogTitle(), successNft1.getDialogMessage(), false);
                                                    ((Dmrc_Card_Request) context).dmrc_customer_cardVO = successNft1;
                                                    ((Dmrc_Card_Request) context).addRequestDmrcCardBanner(successNft1);
                                                }
                                            }));
                                        }));
                                    } else {
                                        if (activity.getClass().getSimpleName().equals("Dmrc_Card_Request")) {
                                            Utility.showSingleButtonDialog(context, dmrc_customer_cardVO.getDialogTitle(), dmrc_customer_cardVO.getDialogMessage(), false);
                                            ((Dmrc_Card_Request) context).dmrc_customer_cardVO = dmrc_customer_cardVO;
                                            ((Dmrc_Card_Request) context).addRequestDmrcCardBanner(dmrc_customer_cardVO);
                                        }
                                    }
                                }));
                            }

                            @Override
                            public void modify(Dialog dialog) {
                                Utility.dismissDialog(context, dialog);
                            }
                        }, context, null, "Your AutoPe DMRC card application will be cancelled.\n" +
                                "Do you want to proceed?", null, new String[]{"Yes", "No"});
                    }
                } else {
                    Utility.showSingleButtonDialogOld(context, "Alert", Content_Message.CONTACT_CUSTOMER_CARE, false);
                }
            }
        });


        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.enableDisableView(view, false);
                findViews(itemView);
                loadAnimations();
                changeCameraDistance();
                if ((clickViewpager.containsKey(position)) && (!clickViewpager.get(position))) {
                    mSetRightOut.setTarget(itemView.findViewById(R.id.card_front));
                    mSetLeftIn.setTarget(itemView.findViewById(R.id.card_back));
                    mSetRightOut.start();
                    mSetLeftIn.start();
                    track.setClickable(false);
                    clickViewpager.put(position, true);
                } else {
                    mSetRightOut.setTarget(itemView.findViewById(R.id.card_back));
                    mSetLeftIn.setTarget(itemView.findViewById(R.id.card_front));
                    mSetRightOut.start();
                    mSetLeftIn.start();
                    track.setClickable(true);
                    clickViewpager.put(position, false);
                }
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utility.enableDisableView(view, true);
                    }
                }, 2000);
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    private void findViews(View v) {
        mCardBackLayout = v.findViewById(R.id.card_back);
        mCardFrontLayout = v.findViewById(R.id.card_front);
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animation);
    }
}
