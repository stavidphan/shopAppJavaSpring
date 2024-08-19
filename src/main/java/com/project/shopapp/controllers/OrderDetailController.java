package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.services.IOrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService orderDetailService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // lấy ra thông tin của 1 order detail
    @GetMapping("/{id}")
    public ResponseEntity <?> getOrderDetail(@Valid @PathVariable("id") Long id) {
        try {
            OrderDetail existingOrderDetail = orderDetailService.getOrderDetailById(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(existingOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // lấy ra danh sách order details của 1 order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("orderId") Long orderId) {
        try {
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
            List<OrderDetailResponse> orderDetailResponses = orderDetails
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .toList();
            return ResponseEntity.ok(orderDetailResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id, @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail updatedOrderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(updatedOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable("id") Long id) {
        try {
            orderDetailService.deleteOrderDetail(id);
            return ResponseEntity.ok(STR."Delete order detail with id = \{id} successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


