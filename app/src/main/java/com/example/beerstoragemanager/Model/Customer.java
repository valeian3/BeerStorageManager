package com.example.beerstoragemanager.Model;

public class Customer {
    private String OrderId;
    private String Name;
    private String CustomerId;

    public Customer(){ }

    public Customer(String customerId, String name, String orderId){
        CustomerId = customerId;
        Name = name;
        OrderId = orderId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }
}
