package com.uav.autodebit.vo;

import java.util.Date;

public class SBICustomerCardVO extends BaseVO {

    private Integer sbiCardId;
    private CustomerVO customer;
    private Integer cardTypeId;
    private String ageGroup;
    private String profession;
    private String grossAnnualIncome;
    private String currentPincode;
    private CityVO city;
    private String cardNo;
    private DmrcCardStatusVO status;

    public SBICustomerCardVO() {
    }

    public Integer getSbiCardId() {
        return sbiCardId;
    }

    public void setSbiCardId(Integer sbiCardId) {
        this.sbiCardId = sbiCardId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public Integer getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Integer cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getGrossAnnualIncome() {
        return grossAnnualIncome;
    }

    public void setGrossAnnualIncome(String grossAnnualIncome) {
        this.grossAnnualIncome = grossAnnualIncome;
    }

    public String getCurrentPincode() {
        return currentPincode;
    }

    public void setCurrentPincode(String currentPincode) {
        this.currentPincode = currentPincode;
    }

    public CityVO getCity() {
        return city;
    }

    public void setCity(CityVO city) {
        this.city = city;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public DmrcCardStatusVO getStatus() {
        return status;
    }

    public void setStatus(DmrcCardStatusVO status) {
        this.status = status;
    }


}
