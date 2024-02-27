package com.example.dw.domain.dto.goods;

import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class RecentViewGoods {


    private Long goodsId;
    private String goodsName;
    private Integer goodsPrice;
    private String goodsImgPath;
    private String goodsImgUuid;
    private String goodsImgName;
    private LocalDateTime lastViewed;


    public RecentViewGoods(Long goodsId, String goodsName, Integer goodsPrice, String goodsImgPath, String goodsImgUuid, String goodsImgName, LocalDateTime lastViewed) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsImgPath = goodsImgPath;
        this.goodsImgUuid = goodsImgUuid;
        this.goodsImgName = goodsImgName;
        this.lastViewed = lastViewed;
    }


    /**
     * 상품 상세페이지로 이동했을 시 최근 본 상품을 session에 담는 메소드
     * @param goodsDetailDto 조회 정보를 담는 dto
     * @param session session 객체
     */
    public void productClicked(GoodsDetailDto goodsDetailDto, HttpSession session) {

        List<RecentViewGoods> recentViews = (List<RecentViewGoods>) session.getAttribute("recentViews");

        // 세션에 최근 본 상품 ID 목록이 없으면 새로운 목록 생성
        if (recentViews == null) {
            recentViews = new ArrayList<>();
        }

        // 상품 ID로 조회된 상품이 이미 최근 본 상품 목록에 있는지 확인
        boolean exists = false;
        for (RecentViewGoods recent : recentViews) {
            if (recent.getGoodsId().equals(goodsDetailDto.getId())) {
                exists = true;
                recent.setLastViewed(LocalDateTime.now()); // 기존에 있던 상품이면 시간 갱신
                break;
            }
        }

        // 기존에 없던 상품이면 목록에 추가
        if (!exists) {
        recentViews.add(new RecentViewGoods(goodsDetailDto.getId(), goodsDetailDto.getGoodsName(), goodsDetailDto.getGoodsPrice(), goodsDetailDto.getGoodsMainImgPath(),
                goodsDetailDto.getGoodsMainImgUuid(), goodsDetailDto.getGoodsMainImgName(), LocalDateTime.now()));
    }

    // 본 시간을 기준으로 내림차순으로 정렬
        recentViews.sort(Comparator.comparing((RecentViewGoods recentViewGoods) -> recentViewGoods.getLastViewed()).reversed());

    // 최근 본 5개의 상품만 유지
    int maxSize = 5;
        if (recentViews.size() > maxSize) {
        recentViews = recentViews.subList(0, maxSize);
    }

        // 세션에 최근 본 상품 목록 저장
        session.setAttribute("recentViews", recentViews);
    }

}
