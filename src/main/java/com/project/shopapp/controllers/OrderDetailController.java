package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO, BindingResult result) {
        return ResponseEntity.ok("create order detail successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity <?> getOrderDetail(@Valid @PathVariable("id") Long id) {
        return ResponseEntity.ok("getOrderDetail with id = " + id);
    }

    // lấy ra danh sách order details của 1 order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok("getOrderDetails with orderId = " + orderId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id, @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok("update order detail with id = " + id + "newOrderDetailData: " + orderDetailDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable("id") Long id) {
        return ResponseEntity.ok("delete order detail with id = " + id);
    }
}


