package com.example.dw.domain.dto.order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.order.QOrderGoodsImgDto is a Querydsl Projection type for OrderGoodsImgDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderGoodsImgDto extends ConstructorExpression<OrderGoodsImgDto> {

    private static final long serialVersionUID = -1047995469L;

    public QOrderGoodsImgDto(com.querydsl.core.types.Expression<String> goodsDetailImgName, com.querydsl.core.types.Expression<String> goodsDetailImgPath, com.querydsl.core.types.Expression<String> goodsDetailImgUuid) {
        super(OrderGoodsImgDto.class, new Class<?>[]{String.class, String.class, String.class}, goodsDetailImgName, goodsDetailImgPath, goodsDetailImgUuid);
    }

}

