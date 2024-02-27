package com.example.dw.controller;

import com.example.dw.domain.dto.admin.AdminOrderDetailResultDto;
import com.example.dw.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminOrderController {


    private final AdminOrderService adminOrderService;


    @GetMapping("/orderStatus")
    public String orderStatus(){
        return "/admin/adminOrderManage";
    }
    @GetMapping("/orderList")
    public String orderList(){
        return "admin/adminOrderList";
    }


    /**
     * 관리자 페이지 - 주문 상세
     * @param userId 회원ID
     * @param orderId 주문ID
     * @param model model
     * @return 주문 상세 정보
     */
    @GetMapping("/orderDetail/{userId}/{orderId}")
    public String orderDetail(
            @PathVariable("userId") Long userId,
            @PathVariable("orderId") Long orderId, Model model){

        AdminOrderDetailResultDto orderDetail = adminOrderService.orderDetail(userId, orderId);

        model.addAttribute("orderDetail", orderDetail);

        return "/admin/adminOrderDetail";

    }




}
