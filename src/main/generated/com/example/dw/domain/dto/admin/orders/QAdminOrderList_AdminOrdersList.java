package com.example.dw.domain.dto.admin.orders;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.admin.admin.QAdminOrderList_AdminOrdersList is a Querydsl Projection type for AdminOrdersListDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QAdminOrderList_AdminOrdersList extends ConstructorExpression<AdminOrderList.AdminOrdersListDto> {

    private static final long serialVersionUID = -954275922L;

    public QAdminOrderList_AdminOrdersList(com.querydsl.core.types.Expression<Long> orderListId, com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> userAccount, com.querydsl.core.types.Expression<String> orderZipcode, com.querydsl.core.types.Expression<String> orderAddress, com.querydsl.core.types.Expression<String> orderDetailAddress, com.querydsl.core.types.Expression<String> orderUserEmail, com.querydsl.core.types.Expression<String> orderUserName, com.querydsl.core.types.Expression<String> orderUserPhone, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderDate, com.querydsl.core.types.Expression<Long> goodsId, com.querydsl.core.types.Expression<String> goodsName, com.querydsl.core.types.Expression<Integer> goodsPrice, com.querydsl.core.types.Expression<Integer> goodsQuantity, com.querydsl.core.types.Expression<java.time.LocalDateTime> payDatetime) {
        super(AdminOrderList.AdminOrdersListDto.class, new Class<?>[]{long.class, long.class, long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, java.time.LocalDateTime.class, long.class, String.class, int.class, int.class, java.time.LocalDateTime.class}, orderListId, orderId, userId, userAccount, orderZipcode, orderAddress, orderDetailAddress, orderUserEmail, orderUserName, orderUserPhone, orderDate, goodsId, goodsName, goodsPrice, goodsQuantity, payDatetime);
    }

}

