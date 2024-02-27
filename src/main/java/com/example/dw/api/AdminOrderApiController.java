package com.example.dw.api;

import com.example.dw.domain.dto.admin.AdminOrderDetailResultDto;
import com.example.dw.domain.dto.admin.AdminWeeklyOrderState;
import com.example.dw.domain.dto.admin.GoodsSaleByCategory;
import com.example.dw.domain.dto.admin.MostOrderUserDto;
import com.example.dw.domain.dto.admin.orders.AdminOrderList;
import com.example.dw.domain.form.AdminSearchOrderForm;
import com.example.dw.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminOrderApiController {




    private final AdminOrderService adminOrderService;


    /**
     * 관리자 페이지 - 일별 주문량 조회
     * @return 일별 주문량
     */
    @GetMapping("/weeklyOrderState")
    public List<AdminWeeklyOrderState> weeklyOrderStateList(){

        return adminOrderService.weeklyOrderStateList();
    }

    /**
     * 관리자 페이지 - 상품 카테고리별 판매비율 조회
     * @return 상품 카테고리별 판매량
     */
    @GetMapping("/salesByCategory")
    public List<GoodsSaleByCategory> salesByCategory(){
        return adminOrderService.saleByCategory();
    }


    /**
     * 관리자 페이지 - 최다 주문 회원 Best5 조회
     * @return 최다 주문 회원 5명 조회
     */
    @GetMapping("/mostOrders")
    public List<MostOrderUserDto> mostOrders(){

        List<MostOrderUserDto> mostOrdersInfo = adminOrderService.mostOrders();

        return mostOrdersInfo;
    }

    /**
     * 관리자 페이지 - 주문 목록
     * @param page page 변수
     * @param adminSearchOrderForm 검색 요건
     * @return 주문 목록
     */
    @GetMapping("/orderList/{page}")
    public Page<AdminOrderList.AdminOrdersListDto.AdminOrderListResultDto> orderList(
            @PathVariable("page")int page,
            AdminSearchOrderForm adminSearchOrderForm){

        Pageable pageable = PageRequest.of(page, 15);

        System.out.println(adminSearchOrderForm+"@@@@@@@@@@@");
        return  adminOrderService.orderList(pageable, adminSearchOrderForm);

    }

    
    //통신확인용
    //관리자 페이지 주문 상세
    @GetMapping("/orderDetail/{userId}/{orderId}")
    public AdminOrderDetailResultDto orderDetail(@PathVariable("userId")Long userId, @PathVariable("orderId") Long orderId){
        return adminOrderService.orderDetail(userId, orderId);
    }
}
