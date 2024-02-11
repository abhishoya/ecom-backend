package org.ecom.common.model.order;

import java.util.*;

public enum OrderStatus
{
    ORDER_CREATE_SUCCESS,
    ORDER_PAYMENT_SUCCESS,
    ORDER_PAYMENT_FAILED,
    ORDER_SHIP_SUCCESS,
    ORDER_SHIP_FAILED,
    ORDER_CANCELLED,
    ORDER_CANCEL_FAILURE,
    ORDER_DELIVER_SUCCESS,
    ORDER_DELIVER_FAILED;

    public boolean isFailed()
    {
        return List.of(ORDER_PAYMENT_FAILED, ORDER_SHIP_FAILED, ORDER_DELIVER_FAILED).contains(this);
    }

    public boolean isSuccess()
    {
        return List.of(ORDER_CREATE_SUCCESS, ORDER_PAYMENT_SUCCESS, ORDER_SHIP_SUCCESS, ORDER_DELIVER_SUCCESS).contains(this);
    }
}
