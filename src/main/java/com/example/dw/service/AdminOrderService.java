package com.example.dw.service;

import com.example.dw.domain.dto.admin.AdminOrderDetailResultDto;
import com.example.dw.domain.dto.admin.AdminWeeklyOrderState;
import com.example.dw.domain.dto.admin.GoodsSaleByCategory;
import com.example.dw.domain.dto.admin.MostOrderUserDto;
import com.example.dw.domain.dto.admin.orders.AdminOrderList;
import com.example.dw.domain.form.AdminSearchOrderForm;
import com.example.dw.repository.admin.AdminOrderRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {


    private final AdminOrderRepositoryCustom adminOrderRepositoryCustom;


    /**
     * 관리자 페이지 - 일별 주문량 조회
     * @return 일별 주문량
     */
    @Transactional
    public List<AdminWeeklyOrderState> weeklyOrderStateList(){

        return adminOrderRepositoryCustom.weeklyOrderState();

    }

    /**
     * 관리자 페이지 - 상품 카테고리별 판매비율 조회
     * @return 상품 카테고리별 판매량
     */
    @Transactional
    public List<GoodsSaleByCategory> saleByCategory(){

        return adminOrderRepositoryCustom.saleByCategory();
    }


    /**
     * 관리자 페이지 - 최다 주문 회원 Best5 조회
     * @return 최다 주문 회원 5명 조회
     */
    @Transactional
    public List<MostOrderUserDto> mostOrders(){

        return adminOrderRepositoryCustom.mostOrders();

    }


    /**
     * 관리자 페이지 - 주문 목록
     * @param pageable 페이징처리를 위한 Pageable
     * @param adminSearchOrderForm 검색 요건
     * @return 주문 목록
     */
    @Transactional
    public Page<AdminOrderList.AdminOrdersListDto.AdminOrderListResultDto> orderList(
            Pageable pageable, AdminSearchOrderForm adminSearchOrderForm){

        return  adminOrderRepositoryCustom.orderLists(pageable, adminSearchOrderForm);

    }

    /**
     * 관리자 페이지 - 주문 상세
     * @param userId 회원ID
     * @param orderId 주문ID
     * @return 주문 상세 정보
     */
    @Transactional
    public AdminOrderDetailResultDto orderDetail(Long userId, Long orderId){

        if (userId == null) {

            throw new IllegalArgumentException("유효하지 않은 회원 번호");
        }

        if (orderId == null) {

            throw new IllegalArgumentException("유효하지 않은 주문 번호");
        }

        return Optional.ofNullable(adminOrderRepositoryCustom.orderDetail(userId,orderId)).orElseThrow(()->{
            throw new IllegalArgumentException("조회 정보 없음");
        });

    }



}
