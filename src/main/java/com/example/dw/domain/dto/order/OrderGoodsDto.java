package com.example.dw.domain.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderGoodsDto {

    private Long goodsId;
    private String goodsName;


    @QueryProjection

    public OrderGoodsDto(Long goodsId, String goodsName) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
    }
}
