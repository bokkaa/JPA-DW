package com.example.dw.repository.admin;

import com.example.dw.domain.dto.admin.goods.AdminGoodsReview;
import com.example.dw.domain.dto.admin.goods.QAdminGoodsReview;
import com.example.dw.domain.dto.admin.goods.QAdminGoodsReview_AdminGoodsReviewApply;
import com.example.dw.domain.dto.admin.goods.QAdminGoodsReview_AdminGoodsReviewDetail;
import com.example.dw.domain.form.SearchReviewForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.goods.QGoods.goods;
import static com.example.dw.domain.entity.goods.QGoodsMainImg.goodsMainImg;
import static com.example.dw.domain.entity.order.QGoodsReviewReply.goodsReviewReply;
import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.order.QOrderReview.orderReview;
import static com.example.dw.domain.entity.order.QOrderReviewImg.orderReviewImg;
import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.user.QUsers.users;

@Repository
@RequiredArgsConstructor
public class AdminGoodsRepositoryImpl implements AdminGoodsRepositoryCustom{



    private final JPQLQueryFactory jpqlQueryFactory;


    /**
     * 관리자 페이지 상품 리뷰 목록
     * @param pageable 페이징처리를 위한 Pageable
     * @param searchReviewForm 검색 카테고리, 키워드
     * @return 상품 리뷰 목록
     */
    @Override
    public Page<AdminGoodsReview.AdminGoodsReviewList.AdminGoodsReviewResultList> reviewList(Pageable pageable, SearchReviewForm searchReviewForm) {


        List<AdminGoodsReview> lists = jpqlQueryFactory.select(
                new QAdminGoodsReview(
                        orderReview.id,
                        goods.goodsCategory.stringValue(),
                        goods.goodsName,
                        orderReview.content,
                        orderReview.rating,
                        orderReview.reviewRd,
                        orderReview.adminReplyState
                )
        )       .from(orderReview)
                .leftJoin(orderReview.orderItem, orderItem)
                .leftJoin(orderItem.goods, goods)
                .where(
                        cateGoryNameEq(searchReviewForm),
                        goodsNameEq(searchReviewForm),
                        createRecruitmentStatusCondition(searchReviewForm)

                )
                .orderBy(orderReview.id.desc())
                .fetch();


        Long getTotal = jpqlQueryFactory.select(
                orderReview.count()
        )
                .from(orderReview)
                .where(
                        cateGoryNameEq(searchReviewForm),
                        goodsNameEq(searchReviewForm),
                        createRecruitmentStatusCondition(searchReviewForm)

                )
                .fetchOne();

        List<AdminGoodsReview.AdminGoodsReviewList.AdminGoodsReviewResultList> result = lists.stream()
                .map(goodsReview ->  new AdminGoodsReview.AdminGoodsReviewList.AdminGoodsReviewResultList(

                goodsReview.getGoodsReviewId(),

                new AdminGoodsReview.AdminGoodsReviewList(

                        goodsReview.getGoodsCategory(),
                        goodsReview.getGoodsName(),
                        goodsReview.getGoodsReviewContent(),
                        goodsReview.getRating(),
                        goodsReview.getGoodsReviewRd(),
                        goodsReview.getReplyState())
                )
        ).collect(Collectors.toList());


        return new PageImpl<>(result, pageable, getTotal);
    }


    /**
     * 상품 기본 정보 및 리뷰 정보
     * @param orderReviewId 주문리뷰ID
     * @return 해당 주문리뷰ID와 일치하는 상품 기본 정보 및 리뷰 정보
     */
    @Override
    public AdminGoodsReview.AdminGoodsReviewDetail.AdminGoodsReviewResultDetail goodsReviewDetail(Long orderReviewId) {

        //리뷰 상품 정보 조회
        AdminGoodsReview.AdminGoodsReviewDetail goodsInfo = Optional.of(jpqlQueryFactory.select(
                new QAdminGoodsReview_AdminGoodsReviewDetail(
                        goods.id,
                        goods.goodsCategory.stringValue(),
                        goods.goodsName,
                        goods.goodsDetailContent,
                        orderReview.rating.avg(),
                        goodsMainImg.id,
                        goodsMainImg.goodsMainImgPath,
                        goodsMainImg.goodsMainImgUuid,
                        goodsMainImg.goodsMainImgName

                )
        )
                .from(orderReview)
                .leftJoin(orderReview.orderItem, orderItem)
                .leftJoin(orderItem.goods, goods)
                .leftJoin(goods.goodsMainImg, goodsMainImg)
                .where(orderReview.id.eq(orderReviewId))
                .groupBy(goods.id, goods.goodsCategory, goods.goodsName, goods.goodsDetailContent,
                        goodsMainImg.id,
                        goodsMainImg.goodsMainImgPath, goodsMainImg.goodsMainImgUuid, goodsMainImg.goodsMainImgName)
                .fetchOne()).orElseThrow(()-> new NoSuchElementException("조회정보없다"));

        
        //리뷰 내용 조회
        List<AdminGoodsReview.AdminGoodsReviewDetail> reviewInfo = Optional.of(jpqlQueryFactory.select(
                new QAdminGoodsReview_AdminGoodsReviewDetail(
                            orderReview.content,
                            users.userAccount,
                            orderReview.rating,
                            orderReview.reviewRd,
                            orderReviewImg.id,
                            orderReviewImg.reviewimgPath,
                            orderReviewImg.reviewimgUuid,
                            orderReviewImg.reviewimgFileName
                                )

        ).from(orderReviewImg)

                .leftJoin(orderReviewImg.orderReview, orderReview)
                .leftJoin(orderReview.orderItem, orderItem)
                .leftJoin(orderItem.orders, orders)
                .leftJoin(orders.users, users)
                .where(orderReview.id.eq(orderReviewId))
                .fetch()).orElseThrow(()-> new NoSuchElementException("조회정보없다"));



        return new AdminGoodsReview.AdminGoodsReviewDetail.AdminGoodsReviewResultDetail(

                orderReviewId,

                new AdminGoodsReview.AdminGoodsReviewDetail.GoodsInfo(

                        goodsInfo.getGoodsId(),
                        goodsInfo.getGoodsCategory(),
                        goodsInfo.getGoodsName(),
                        goodsInfo.getGoodsDetailContent(),
                        goodsInfo.getRatingAvg(),
                        new AdminGoodsReview.AdminGoodsReviewDetail.GoodsImg(

                                goodsInfo.getGoodsMainImgId(),
                                goodsInfo.getGoodsMainImgPath(),
                                goodsInfo.getGoodsMainImgUuid(),
                                goodsInfo.getGoodsMainImgName())

                ),
                reviewInfo.stream().map(reviews -> new AdminGoodsReview.AdminGoodsReviewDetail.ReviewContent(

                        reviews.getOrderReviewContent(),
                        reviews.getUserAccount(),
                        reviews.getRating(),
                        reviews.getOrderReviewRd(),

                        reviewInfo.stream().map(o->new AdminGoodsReview.AdminGoodsReviewDetail.ReviewImg(
                                o.getOrderReviewImgId(),
                                o.getOrderReviewImgPath(),
                                o.getOrderReviewImgUuid(),
                                o.getOrderReviewImgName()

                        )).collect(Collectors.toList()))).findFirst().orElseThrow(()->new NoSuchElementException("조회정보없음"))
        );


    }

    /**
     * 상품 리뷰에 등록된 관리자 답변 조회
     * @param orderReviewId 주문 리뷰ID
     * @return 해당 주문 리뷰ID와 일치하는 상품 리뷰
     */
    @Override
    public AdminGoodsReview.AdminGoodsReviewApply goodsReviewReplyList(Long orderReviewId) {
        return jpqlQueryFactory.select(
                new QAdminGoodsReview_AdminGoodsReviewApply(
                        goodsReviewReply.id,
                        goodsReviewReply.goodsReviewReplyContent,
                        goodsReviewReply.goodsReviewReplyRD,
                        goodsReviewReply.goodsReviewReplyMD
                )
        )
                .from(goodsReviewReply)
                .where(goodsReviewReply.orderReview.id.eq(orderReviewId))
                .fetchOne();
    }


    /**
     * 리뷰 상품 검색 카테고리
     * @param searchReviewForm 상품 카테고리
     * @return
     */
    private BooleanExpression cateGoryNameEq(SearchReviewForm searchReviewForm){
        return StringUtils.hasText(searchReviewForm.getCate()) ? orderItem.goods.goodsCategory.stringValue().eq(searchReviewForm.getCate()) : null;
    }

    /**
     * 리뷰 상품 검색 키워드
     * @param searchReviewForm 상품명 키워드
     * @return
     */
    private BooleanExpression goodsNameEq(SearchReviewForm searchReviewForm){
        return StringUtils.hasText(searchReviewForm.getKeyword()) ? orderItem.goods.goodsName.containsIgnoreCase(searchReviewForm.getKeyword()) : null;
    }


    /**
     * 상품 리뷰 답변 완료 / 대기 상태값
     * @param searchReviewForm 답변 상태값이 담긴 form
     * @return
     */
    private BooleanExpression createRecruitmentStatusCondition(SearchReviewForm searchReviewForm) {
        try{
            if (searchReviewForm.getAdminReplyState().equals("0")) { // 답변 대기중
                return orderReview.adminReplyState.eq(0);
            } else if (searchReviewForm.getAdminReplyState().equals("1")) { // 답변 완료
                return orderReview.adminReplyState.eq(1);
            } else { // 전체보기 ('' 인 경우)
                return null; // 특정 조건 없이 모든 결과 반환
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }



}


