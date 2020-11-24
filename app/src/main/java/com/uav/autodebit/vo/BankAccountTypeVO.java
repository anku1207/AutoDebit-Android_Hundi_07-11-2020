package com.uav.autodebit.vo;

import java.io.Serializable;

public class BankAccountTypeVO extends BaseVO implements Serializable {

    private Integer accountTypeId;
    private String accountName;
    private StatusVO status;

    public BankAccountTypeVO() {
    }

    public Integer getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Integer accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public StatusVO getStatus() {
        return status;
    }

    public void setStatus(StatusVO status) {
        this.status = status;
    }
}
