package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class IRCTCBO implements Serializable {
    public static ConnectionVO getPaymentOpationForService() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getPaymentOpationForService");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO getIRCTCPGURLs() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getIRCTCPGURLs");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO proceedIRCTCMandatePGResponse() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("proceedIRCTCMandatePGResponse");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getMandateListByPaymentOption() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getMandateListByPaymentOption");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO createIRCTCBankMandateRequest() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("createIRCTCBankMandateRequest");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO updateIRCTCBankMandateStatus() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("updateIRCTCBankMandateStatus");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO updateIRCTCDefaultMandate() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("updateIRCTCDefaultMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }








}
