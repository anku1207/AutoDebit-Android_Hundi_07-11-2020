package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class BeneficiaryBO implements Serializable {
    public static ConnectionVO confirmationAfterPenyDrop() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("confirmationAfterPenyDrop");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO p2pInitiateAmount() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("p2pInitiateAmount");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO getCustomerBeneficiaryList() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("fetchBeneficiaryToCustomer");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}
