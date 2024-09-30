package com.project.shopapp.services;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
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
        order.setOrderDate(LocalDate.now());  // lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);

        // tạo danh sách các đối tượng OrderDetail từ CartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ CSDL
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException(STR."Cannot find product with id: \{productId}"));

            // Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            // Các trường khác của orderdetail nếu cần
            orderDetail.setPrice(product.getPrice());

            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrderById(Long id) throws Exception {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(STR."Cannot find order with id: \{id}"));
    }

    @Override
    @Transactional
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
    @Transactional
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

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
