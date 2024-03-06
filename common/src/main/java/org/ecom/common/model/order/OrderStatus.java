package org.ecom.common.model.order;

import java.util.List;

public enum OrderStatus
{
    ORDER_CREATED,
    ORDER_PAYMENT_SUCCESS,
    ORDER_PAYMENT_FAILED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED;

    public boolean isFailed()
    {
        return List.of(ORDER_PAYMENT_FAILED, ORDER_CANCELLED).contains(this);
    }

    public boolean isSuccess()
    {
        return List.of(ORDER_CREATED, ORDER_PAYMENT_SUCCESS, ORDER_SHIPPED, ORDER_DELIVERED).contains(this);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
