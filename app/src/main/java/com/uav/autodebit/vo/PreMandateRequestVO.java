package com.uav.autodebit.vo;

import java.io.Serializable;

public class PreMandateRequestVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer requestId;
    private Long transactionDate;
    private ServiceTypeVO serviceType;
    private Double siTransactionAmount;
    private Double otmTransactionAmount;
    private String currency;
    private StatusVO status;
    private CustomerVO customer;
    private String exp;
    private String merchantName;
    private String paymentMode;
    private String payId;
    private String payMode;
    private String productDesc;
    private String returnURL;
    private String txnType;
    private String whatsAppFlag;
    private String cardFlag;
    private String enachURL;
    private String url;
    private CustomerAuthServiceVO customerAuth;
    private String mandateType;

    public PreMandateRequestVO() {
    }


    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Long getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Long transactionDate) {
        this.transactionDate = transactionDate;
    }

    public ServiceTypeVO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeVO serviceType) {
        this.serviceType = serviceType;
    }

    public Double getSiTransactionAmount() {
        return siTransactionAmount;
    }

    public void setSiTransactionAmount(Double siTransactionAmount) {
        this.siTransactionAmount = siTransactionAmount;
    }

    public Double getOtmTransactionAmount() {
        return otmTransactionAmount;
    }

    public void setOtmTransactionAmount(Double otmTransactionAmount) {
        this.otmTransactionAmount = otmTransactionAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public StatusVO getStatus() {
        return status;
    }

    public void setStatus(StatusVO status) {
        this.status = status;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getWhatsAppFlag() {
        return whatsAppFlag;
    }

    public void setWhatsAppFlag(String whatsAppFlag) {
        this.whatsAppFlag = whatsAppFlag;
    }

    public String getCardFlag() {
        return cardFlag;
    }

    public void setCardFlag(String cardFlag) {
        this.cardFlag = cardFlag;
    }

    public String getEnachURL() {
        return enachURL;
    }

    public void setEnachURL(String enachURL) {
        this.enachURL = enachURL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CustomerAuthServiceVO getCustomerAuth() {
        return customerAuth;
    }

    public void setCustomerAuth(CustomerAuthServiceVO customerAuth) {
        this.customerAuth = customerAuth;
    }

    public String getMandateType() {
        return mandateType;
    }

    public void setMandateType(String mandateType) {
        this.mandateType = mandateType;
    }
}
