package com.example.dw.domain.form;

import com.example.dw.domain.entity.goods.Goods;
import com.example.dw.domain.entity.goods.GoodsCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoodsForm {

    private Long id;
    private String goodsName;
    private int goodsQuantity;
    private int goodsPrice;
    private String goodsMade;
    private String goodsCertify;
    private String goodsDetailContent;
    private GoodsCategory goodsCategory;


    @Builder
    public GoodsForm(Long id, String goodsName, int goodsQuantity, int goodsPrice, String goodsMade,
                     String goodsCertify, String goodsDetailContent, GoodsCategory goodsCategory) {
        this.id = id;
        this.goodsName = goodsName;
        this.goodsQuantity = goodsQuantity;
        this.goodsPrice = goodsPrice;
        this.goodsMade = goodsMade;
        this.goodsCertify = goodsCertify;
        this.goodsDetailContent = goodsDetailContent;
        this.goodsCategory = goodsCategory;
    }

    public Goods toEntity(){
        return Goods.builder()
                .id(id)
                .goodsName(goodsName)
                .goodsPrice(goodsPrice)
                .goodsMade(goodsMade)
                .goodsQuantity(goodsQuantity)
                .goodsCertify(goodsCertify)
                .goodsCategory(goodsCategory)
                .goodsDetailContent(goodsDetailContent)
                .build();
    }


}
