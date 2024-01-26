package com.example.dw.domain.dto.order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.order.QOrderGoodsDto is a Querydsl Projection type for OrderGoodsDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderGoodsDto extends ConstructorExpression<OrderGoodsDto> {

    private static final long serialVersionUID = -843578200L;

    public QOrderGoodsDto(com.querydsl.core.types.Expression<Long> goodsId, com.querydsl.core.types.Expression<String> goodsName) {
        super(OrderGoodsDto.class, new Class<?>[]{long.class, String.class}, goodsId, goodsName);
    }

}

