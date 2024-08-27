package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // kiểm tra userId có tồn tại không
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(STR."Cannot find user with id: \{orderDTO.getUserId()}"));

        // kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now().plusDays(1) : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping date must be >= today");
        }
        orderDTO.setShippingDate(shippingDate);

        //convert userDTO -> user
        Order order = new Order();
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());  // lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrderById(Long id) throws Exception {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(STR."Cannot find order with id: \{id}"));
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(STR."Cannot find order with id: \{id}"));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(STR."Cannot find user with id: \{orderDTO.getUserId()}"));
        // kiểm tra shipping date phải >= ngày hôm nay
        if (orderDTO.getShippingDate().isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping date must be >= today");
        }

        //convert userDTO -> user
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(existingUser);

        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        // ko xoa that su ma chi update trang thai active
        if (order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    // Tìm tất cả đơn hàng của 1 user
    @Override
    public List<Order> findByUserId(Long userId) throws Exception {
        // kiểm tra userId có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(STR."Cannot find user with id: \{userId}"));
        return orderRepository.findByUserId(userId);
    }
}
