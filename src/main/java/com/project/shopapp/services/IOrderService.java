package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrderById(Long id) throws Exception;
    Order updateOrder(Long id, OrderDTO orderDTO) throws Exception;
    void deleteOrder(Long id);
    List<Order> findByUserId(Long userId) throws Exception;
}
