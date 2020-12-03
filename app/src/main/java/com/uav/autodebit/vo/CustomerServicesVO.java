package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerServicesVO extends BaseVO implements Serializable {


    private Integer csId;
    private CustomerVO customer;
    private ServiceTypeVO serviceType;
    private StatusVO status;
    private CustomerAuthServiceVO customerAuthService;
    private CustomerAuthServiceVO siMandate;
    private CustomerAuthServiceVO upiRecurring;
    private CustomerAuthServiceVO defaultAuth;


    public CustomerServicesVO() {
    }


    public Integer getCsId() {
        return csId;
    }

    public void setCsId(Integer csId) {
        this.csId = csId;
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

    public StatusVO getStatus() {
        return status;
    }

    public void setStatus(StatusVO status) {
        this.status = status;
    }

    public CustomerAuthServiceVO getCustomerAuthService() {
        return customerAuthService;
    }

    public void setCustomerAuthService(CustomerAuthServiceVO customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    public CustomerAuthServiceVO getSiMandate() {
        return siMandate;
    }

    public void setSiMandate(CustomerAuthServiceVO siMandate) {
        this.siMandate = siMandate;
    }

    public CustomerAuthServiceVO getUpiRecurring() {
        return upiRecurring;
    }

    public void setUpiRecurring(CustomerAuthServiceVO upiRecurring) {
        this.upiRecurring = upiRecurring;
    }

    public CustomerAuthServiceVO getDefaultAuth() {
        return defaultAuth;
    }

    public void setDefaultAuth(CustomerAuthServiceVO defaultAuth) {
        this.defaultAuth = defaultAuth;
    }
}
