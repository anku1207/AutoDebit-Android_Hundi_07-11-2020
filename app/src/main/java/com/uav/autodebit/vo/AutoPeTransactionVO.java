package com.uav.autodebit.vo;

import java.io.Serializable;

public class AutoPeTransactionVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer aptxnId;
    private CustomerVO customer;
    private ServiceTypeVO serviceType;
    private Double amount;
    private String enityType;
    private Integer enityTxnId;
    private Long enityTxnDate;
    private AutoPeTransactionVO parentId;
    private String createdBy;
    private Long debitDate;
    private Integer txnId;
    private String txnEnity;
    private Integer debitTimes;
    private Integer debitSuccessFull;
    private Integer notificationSent;
    private Integer drNotificationSent;


    public Integer getAptxnId() {
        return aptxnId;
    }

    public void setAptxnId(Integer aptxnId) {
        this.aptxnId = aptxnId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public ServiceTypeVO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeVO serviceType) {
        this.serviceType = serviceType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getEnityType() {
        return enityType;
    }

    public void setEnityType(String enityType) {
        this.enityType = enityType;
    }

    public Integer getEnityTxnId() {
        return enityTxnId;
    }

    public void setEnityTxnId(Integer enityTxnId) {
        this.enityTxnId = enityTxnId;
    }

    public Long getEnityTxnDate() {
        return enityTxnDate;
    }

    public void setEnityTxnDate(Long enityTxnDate) {
        this.enityTxnDate = enityTxnDate;
    }

    public AutoPeTransactionVO getParentId() {
        return parentId;
    }

    public void setParentId(AutoPeTransactionVO parentId) {
        this.parentId = parentId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getDebitDate() {
        return debitDate;
    }

    public void setDebitDate(Long debitDate) {
        this.debitDate = debitDate;
    }

    public Integer getTxnId() {
        return txnId;
    }

    public void setTxnId(Integer txnId) {
        this.txnId = txnId;
    }

    public String getTxnEnity() {
        return txnEnity;
    }

    public void setTxnEnity(String txnEnity) {
        this.txnEnity = txnEnity;
    }


    public Integer getDebitTimes() {
        return debitTimes;
    }

    public void setDebitTimes(Integer debitTimes) {
        this.debitTimes = debitTimes;
    }

    public Integer getDebitSuccessFull() {
        return debitSuccessFull;
    }

    public void setDebitSuccessFull(Integer debitSuccessFull) {
        this.debitSuccessFull = debitSuccessFull;
    }

    public Integer getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Integer notificationSent) {
        this.notificationSent = notificationSent;
    }

    public Integer getDrNotificationSent() {
        return drNotificationSent;
    }

    public void setDrNotificationSent(Integer drNotificationSent) {
        this.drNotificationSent = drNotificationSent;
    }
}
