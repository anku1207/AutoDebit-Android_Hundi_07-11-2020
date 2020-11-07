package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerServiceOperatorVO extends BaseVO implements Serializable {

    private Integer cSOId;
    private CustomerVO customer;
    private ServiceTypeVO serviceType;
    private String operaterName;
    private Double amount;
    private Integer paymnetScheduleDays;
    private String  billFetchRequest;

    private Long createdAt;
    private Long billDate;
    private Long dueDate;
    private Long lastUpdateDate;
    private Long estimateBillDate;
    private StatusVO status;


    public Integer getcSOId() {
        return cSOId;
    }

    public void setcSOId(Integer cSOId) {
        this.cSOId = cSOId;
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

    public String getOperaterName() {
        return operaterName;
    }

    public void setOperaterName(String operaterName) {
        this.operaterName = operaterName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPaymnetScheduleDays() {
        return paymnetScheduleDays;
    }

    public void setPaymnetScheduleDays(Integer paymnetScheduleDays) {
        this.paymnetScheduleDays = paymnetScheduleDays;
    }

    public String getBillFetchRequest() {
        return billFetchRequest;
    }

    public void setBillFetchRequest(String billFetchRequest) {
        this.billFetchRequest = billFetchRequest;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getBillDate() {
        return billDate;
    }

    public void setBillDate(long billDate) {
        this.billDate = billDate;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getEstimateBillDate() {
        return estimateBillDate;
    }

    public void setEstimateBillDate(long estimateBillDate) {
        this.estimateBillDate = estimateBillDate;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public StatusVO getStatus() {
        return status;
    }

    public void setStatus(StatusVO status) {
        this.status = status;
    }
}
