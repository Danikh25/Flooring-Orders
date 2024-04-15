package org.example.dao;

import org.example.dto.Order;

import java.time.LocalDate;
import java.util.List;

/**
 * getOrderId
 */
public interface OrderDao {

    public  Order getOrderId(LocalDate date, int orderNumber);
    public List<Order> getAllOrders();
    public List<Order> getAllOrdersDate(LocalDate date);
    public Order createOrder(Order order);
    public Order updateOrder(Order order);
    public Order deleteOrder(LocalDate date, int orderNumber);
    //public void exportAllData();
}
