package com.uav.autodebit.vo;

import java.io.Serializable;
import java.util.Date;

public class CustomerAuthServiceVO extends BaseVO implements Serializable {
    private String providerTokenId;
    private CustomerVO customer;


    private Integer customerAuthId;
    private AuthStatusVO authStatus;
    private String accountNumber;
    private String  bankName;
    private String  accountHolderName;
    private Double mandateAmount;

    //New Fesd 14 2021
    private Long debitStartDate;
    private String urmn;
    private String accountType;
    private String cardNo;
    private String cardNoMask;





    public CustomerAuthServiceVO() {
    }


    public String getProviderTokenId() {
        return providerTokenId;
    }

    public void setProviderTokenId(String providerTokenId) {
        this.providerTokenId = providerTokenId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }


    public Integer getCustomerAuthId() {
        return customerAuthId;
    }

    public void setCustomerAuthId(Integer customerAuthId) {
        this.customerAuthId = customerAuthId;
    }

    public AuthStatusVO getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(AuthStatusVO authStatus) {
        this.authStatus = authStatus;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Double getMandateAmount() {
        return mandateAmount;
    }

    public void setMandateAmount(Double mandateAmount) {
        this.mandateAmount = mandateAmount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Long getDebitStartDate() {
        return debitStartDate;
    }

    public void setDebitStartDate(Long debitStartDate) {
        this.debitStartDate = debitStartDate;
    }

    public String getUrmn() {
        return urmn;
    }

    public void setUrmn(String urmn) {
        this.urmn = urmn;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardNoMask() {
        return cardNoMask;
    }

    public void setCardNoMask(String cardNoMask) {
        this.cardNoMask = cardNoMask;
    }
}
