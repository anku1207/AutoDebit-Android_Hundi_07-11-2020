package com.uav.autodebit.vo;

public class BeneAccVO extends  BaseVO{

    private Integer beneficialAccountId;
    private String beneficialPhone;
    private String beneficialAccountNum;
    private String beneficialIFSCcode;
    private String authCode;
    private StatusVO status;
    private String txnId;
    private String acqId;
    private String pgRefNum;
    private String beneNameRespose;
    private String createdBy;
    private long  createdAt;
    private BankAccountTypeVO bankAccountTypeVO;



    public BeneAccVO() {
    }

    public Integer getBeneficialAccountId() {
        return beneficialAccountId;
    }

    public void setBeneficialAccountId(Integer beneficialAccountId) {
        this.beneficialAccountId = beneficialAccountId;
    }

    public String getBeneficialPhone() {
        return beneficialPhone;
    }

    public void setBeneficialPhone(String beneficialPhone) {
        this.beneficialPhone = beneficialPhone;
    }

    public String getBeneficialAccountNum() {
        return beneficialAccountNum;
    }

    public void setBeneficialAccountNum(String beneficialAccountNum) {
        this.beneficialAccountNum = beneficialAccountNum;
    }

    public String getBeneficialIFSCcode() {
        return beneficialIFSCcode;
    }

    public void setBeneficialIFSCcode(String beneficialIFSCcode) {
        this.beneficialIFSCcode = beneficialIFSCcode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public StatusVO getStatus() {
        return status;
    }

    public void setStatus(StatusVO status) {
        this.status = status;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getAcqId() {
        return acqId;
    }

    public void setAcqId(String acqId) {
        this.acqId = acqId;
    }

    public String getPgRefNum() {
        return pgRefNum;
    }

    public void setPgRefNum(String pgRefNum) {
        this.pgRefNum = pgRefNum;
    }

    public String getBeneNameRespose() {
        return beneNameRespose;
    }

    public void setBeneNameRespose(String beneNameRespose) {
        this.beneNameRespose = beneNameRespose;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public BankAccountTypeVO getBankAccountTypeVO() {
        return bankAccountTypeVO;
    }

    public void setBankAccountTypeVO(BankAccountTypeVO bankAccountTypeVO) {
        this.bankAccountTypeVO = bankAccountTypeVO;
    }
}
