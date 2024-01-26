package com.example.dw.domain.dto.order;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderGoodsImgDto {


    private String goodsDetailImgName;
    private String goodsDetailImgPath;
    private String goodsDetailImgUuid;


    @QueryProjection
    public OrderGoodsImgDto(String goodsDetailImgName, String goodsDetailImgPath, String goodsDetailImgUuid) {
        this.goodsDetailImgName = goodsDetailImgName;
        this.goodsDetailImgPath = goodsDetailImgPath;
        this.goodsDetailImgUuid = goodsDetailImgUuid;
    }
}
