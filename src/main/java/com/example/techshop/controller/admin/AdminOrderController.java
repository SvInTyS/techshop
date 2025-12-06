package com.example.techshop.controller.admin;

import com.example.techshop.domain.OrderStatus;
import com.example.techshop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.findAllOrders());
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        var order = orderService.findById(id);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/view";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") OrderStatus status) {

        var order = orderService.findById(id);
        if (order != null) {
            order.setStatus(status);
            orderService.save(order);
        }

        return "redirect:/admin/orders/" + id;
    }
}