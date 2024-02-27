package com.example.dw.repository.admin;

import com.example.dw.domain.dto.admin.AdminOrderDetailResultDto;
import com.example.dw.domain.dto.admin.AdminWeeklyOrderState;
import com.example.dw.domain.dto.admin.GoodsSaleByCategory;
import com.example.dw.domain.dto.admin.MostOrderUserDto;
import com.example.dw.domain.dto.admin.orders.AdminOrderList;
import com.example.dw.domain.form.AdminSearchOrderForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminOrderRepositoryCustom {


    //관리자 페이지 주문 리스트
    Page<AdminOrderList.AdminOrdersListDto.AdminOrderListResultDto> orderLists(Pageable pageable, AdminSearchOrderForm adminSearchOrderForm);


    //관리자 페이지 주문 상세
    AdminOrderDetailResultDto orderDetail(Long userId, Long orderId);


    //일별 주문 현황
    List<AdminWeeklyOrderState> weeklyOrderState();
    
    //상품 카테고리별 판매 비율
    List<GoodsSaleByCategory> saleByCategory();

    //최다 주문 회원
    List<MostOrderUserDto> mostOrders();
}
