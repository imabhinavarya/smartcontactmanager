package com.smart.entities;

import javax.persistence.*;

@Entity
@Table(name="orders")
public class MyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long myOrderId;
    private String orderId;
    private String amount;
    private String receipt;
    private String status;
    @ManyToOne
    private User user;
    private String paymentId;

    public MyOrder() {
    }

    public MyOrder(Long myOrderId, String orderId, String amount, String receipt, String status, User user, String paymentId) {
        this.myOrderId = myOrderId;
        this.orderId = orderId;
        this.amount = amount;
        this.receipt = receipt;
        this.status = status;
        this.user = user;
        this.paymentId = paymentId;
    }

    public Long getMyOrderId() {
        return myOrderId;
    }

    public void setMyOrderId(Long myOrderId) {
        this.myOrderId = myOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "MyOrder{" +
                "myOrderId=" + myOrderId +
                ", orderId='" + orderId + '\'' +
                ", amount='" + amount + '\'' +
                ", receipt='" + receipt + '\'' +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", paymentId='" + paymentId + '\'' +
                '}';
    }
}
