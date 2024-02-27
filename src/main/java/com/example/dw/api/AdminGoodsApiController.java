package com.example.dw.api;


import com.example.dw.controller.Message;
import com.example.dw.domain.dto.admin.AdminGoodsQnaListDto;
import com.example.dw.domain.dto.admin.AdminGoodsQueReplyDto;
import com.example.dw.domain.dto.admin.goods.AdminGoods;
import com.example.dw.domain.dto.admin.goods.AdminGoodsReview;
import com.example.dw.domain.enums.StatusEnum;
import com.example.dw.domain.form.GoodsQueReplyForm;
import com.example.dw.domain.form.GoodsReviewReplyForm;
import com.example.dw.domain.form.SearchForm;
import com.example.dw.domain.form.SearchReviewForm;
import com.example.dw.repository.goods.GoodsRepositoryCustom;
import com.example.dw.service.AdminGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/*")
public class AdminGoodsApiController {

    private final AdminGoodsService adminGoodsService;
    private final GoodsRepositoryCustom goodsRepositoryCustom;

    /**
     *
     * @param page page 변수
     * @param searchForm 검색 카테고리, 키워드가 담겨져 있는 클래스
     * @return 등록된 상품 목록
     */
    @GetMapping("/goodsList/{page}")
    public ResponseEntity<Message> findGoodsList(
            @PathVariable("page") int page, SearchForm searchForm){

        Pageable pageable = PageRequest.of(page, 15);
        Page<AdminGoods.AdminGoodsList> result = goodsRepositoryCustom.findGoodsAll(pageable, searchForm);

        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");
        message.setData(result);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

    //post맨 실험용
    //상품 상세 페이지 이동
    @GetMapping("/detail/{goodsId}")
    public AdminGoods.AdminGoodsDetail goodsDetail(@PathVariable("goodsId") Long goodsId){




        return adminGoodsService.goodsDetail(goodsId);


    }

    /**
     * 관리자 페이지 상품 관련 문의 목록
     * @param page page 변수
     * @param qnaState 해당 문의에 대한 관리자 답변 여부
     * @param cate 검색 카테고리
     * @param keyword 검색 키워드
     * @return
     */
    @GetMapping("/goodsQnaList/{page}")
    public Page<AdminGoodsQnaListDto> findGoodsQnaList(@PathVariable("page") int page,
                                                       String qnaState,
                                                       String cate,
                                                       String keyword){

        Pageable pageable = PageRequest.of(page, 15);

        Page<AdminGoodsQnaListDto> qnaLists = adminGoodsService.getGoodsQnaList(pageable, qnaState, cate, keyword);

        return qnaLists;

    }


    /**
     *
     * @param goodsId 상품ID
     * @param page page 변수
     * @param state 상품 문의 답변 여부 확인
     * @return 해당 상품 관련 문의 리스트
     */
    @GetMapping("/goodsRelatedQna/{goodsId}/{page}")
    public Page<AdminGoodsQnaListDto> findGoodsDetailQnaList(
            @PathVariable("goodsId") Long goodsId,
            @PathVariable("page") int page,
            String state){

        Pageable pageable = PageRequest.of(page, 10);

        return adminGoodsService.findGoodsDetailQnaList(goodsId, pageable, state);

    }

    /**
     *
     * @param goodsId 상품ID
     * @param page page 변수
     * @param state 상품 리뷰 답변 여부 확인
     * @return 해당 상품 관련 리뷰 리스트
     */
    @GetMapping("/goodsRelatedReview/{goodsId}/{page}")
    public Page<AdminGoodsReview.AdminGoodsRelatedReview> findGoodsDetailReviewList(
            @PathVariable("goodsId") Long goodsId,
            @PathVariable("page") int page,
            String state
    ){

        Pageable pageable = PageRequest.of(page, 10);

        return adminGoodsService.findGoodsDetailReviewList(goodsId, pageable, state);

    }


    /**
     * 상품 문의 관리자 답변 등록
     * @param goodsQueReplyForm 관리자 답변에 대한 정보가 담긴 form
     */
    @PostMapping("/addQnaReply")
    public void addQnaReply(GoodsQueReplyForm goodsQueReplyForm){


        adminGoodsService.addQnaReply(goodsQueReplyForm);

    }

    /**
     * 상품 문의 관리자 답변 가져오기
     * @param qnaId 상품문의ID
     * @return 해당 문의사항에 대한 관리자 답변
     */
    @GetMapping("/replyList/{qnaId}")
    public AdminGoodsQueReplyDto replyList(@PathVariable("qnaId") Long qnaId){

        return adminGoodsService.replyList(qnaId);

    }

    /**
     * 상품 문의 관리자 답변 수정
     * @param goodsQueReplyForm 해당 문의사항에 관환 답변내용 수정 사항이 담긴 form
     */
    @PatchMapping("/replyModify")
    public void replyModify(GoodsQueReplyForm goodsQueReplyForm){

        adminGoodsService.replyModify(goodsQueReplyForm);


    }

    /**
     * 상품 문의 관리자 답변 삭제
     * @param replyId 상품 문의 답변 ID
     */
    @DeleteMapping("/replyDelete/{replyId}")
    public void replyDelete(@PathVariable("replyId") Long replyId){

        adminGoodsService.replyDelete(replyId);
    }


    /**
     * 관리자 페이지 상품 리뷰 목록
     * @param page 페이지 변수
     * @param searchReviewForm 검색 카테고리, 키워드
     * @return 상품 리뷰 목록
     */
    @GetMapping("/goodsReviewList/{page}")
    public Page<AdminGoodsReview.AdminGoodsReviewList.AdminGoodsReviewResultList> goodsReviewList(@PathVariable("page") int page,
                                                                                                  SearchReviewForm searchReviewForm){

        Pageable pageable = PageRequest.of(page, 15);

        return adminGoodsService.reviewList(pageable, searchReviewForm);

    }

    //관리자 상품 리뷰 상세(통신용)
    @GetMapping("/goodsReviewDetail/{orderReviewId}")
    public AdminGoodsReview.AdminGoodsReviewDetail.AdminGoodsReviewResultDetail reviewDetail(@PathVariable("orderReviewId") Long orderReviewId){
        return adminGoodsService.reviewDetail(orderReviewId);
    }

    /**
     * 관리자 페이지 - 리뷰 -> 관리자 답변 등록
     * @param goodsReviewReplyForm 관리자 답변 정보가 담긴 form
     */
    @PostMapping("/addGoodsReviewReply")
    public void addGoodsReviewReply(GoodsReviewReplyForm goodsReviewReplyForm){

        System.out.println(goodsReviewReplyForm.toString()+"!#@!#!@");

        adminGoodsService.addGoodsReviewReply(goodsReviewReplyForm);

    }

    /**
     * 관리자 페이지 - 리뷰 -> 관리자 답변 조회
     * @param orderReviewId 주문 리뷰ID
     * @return 관리자 답변 정보
     */
    @GetMapping("/goodsReviewReplyList/{orderReviewId}")
    public AdminGoodsReview.AdminGoodsReviewApply goodsReviewReply(@PathVariable("orderReviewId")Long orderReviewId){

        return adminGoodsService.goodsReviewReplyList(orderReviewId);
    }

    /**
     * 관리자 페이지 - 리뷰 -> 관리자 답변 삭제
     * @param replyId 답변ID
     * @param orderReviewId  주문리뷰ID
     */
    @DeleteMapping("/deleteGoodsReviewReply/{replyId}/{orderReviewId}")
    public void deleteGoodsReviewReply(@PathVariable("replyId") Long replyId,
                                       @PathVariable("orderReviewId") Long orderReviewId){

        adminGoodsService.goodsReviewReplyDelete(replyId, orderReviewId);

    }

    /**
     * 관리자 페이지 - 리뷰 -> 관리자 답변 수정
     * @param modContent 수정된 답변 내용
     * @param id 답변ID
     */
    @PatchMapping("/modifyingGoodsReviewReply")
    public void modifyGoodsReviewReply(String modContent, Long id){
        GoodsReviewReplyForm form = new GoodsReviewReplyForm();
        form.setGoodsReviewReplyContent(modContent);
        form.setId(id);


        System.out.println(form.toString()+"!@#!@#!@#");

        adminGoodsService.goodsReviewReplyModify(form);

    }

}
