package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerServiceOperatorBillerFilterVO extends BaseVO implements Serializable {

    private Integer cSOBFId;
    private CustomerVO customer;
    private ServiceTypeVO serviceType;
    private CustomerServiceOperatorVO customerServiceOperator;
    private String filterKey;
    private String filterValue;

    public Integer getcSOBFId() {
        return cSOBFId;
    }

    public void setcSOBFId(Integer cSOBFId) {
        this.cSOBFId = cSOBFId;
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

    public CustomerServiceOperatorVO getCustomerServiceOperator() {
        return customerServiceOperator;
    }

    public void setCustomerServiceOperator(CustomerServiceOperatorVO customerServiceOperator) {
        this.customerServiceOperator = customerServiceOperator;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
}
