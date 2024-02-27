package com.example.dw.service;

import com.example.dw.domain.dto.admin.AdminGoodsQnaListDto;
import com.example.dw.domain.dto.admin.AdminGoodsQueDetailDto;
import com.example.dw.domain.dto.admin.AdminGoodsQueReplyDto;
import com.example.dw.domain.dto.admin.goods.AdminGoods;
import com.example.dw.domain.dto.admin.goods.AdminGoodsReview;
import com.example.dw.domain.entity.goods.Goods;
import com.example.dw.domain.entity.goods.GoodsQue;
import com.example.dw.domain.entity.goods.GoodsQueReply;
import com.example.dw.domain.form.GoodsForm;
import com.example.dw.domain.form.GoodsQueReplyForm;
import com.example.dw.domain.form.GoodsReviewReplyForm;
import com.example.dw.domain.form.SearchReviewForm;
import com.example.dw.repository.admin.AdminGoodsRepositoryCustom;
import com.example.dw.repository.admin.AdminGoodsReviewRepository;
import com.example.dw.repository.goods.GoodsQueReplyRepository;
import com.example.dw.repository.goods.GoodsQueRepository;
import com.example.dw.repository.goods.GoodsRepository;
import com.example.dw.repository.goods.GoodsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminGoodsService {

    private final AdminGoodsReviewRepository adminGoodsReviewRepository;
    private final AdminGoodsRepositoryCustom adminGoodsRepositoryCustom;
    private final GoodsQueRepository goodsQueRepository;
    private final GoodsQueReplyRepository goodsQueReplyRepository;
    private final GoodsRepositoryCustom goodsRepositoryCustom;
    private final GoodsRepository goodsRepository;
    private final FileService fileService;


    /**
     * 
     * @param goodsForm 상품 기본 입력 정보
     * @return 등록 성공 시 반환되는 상품ID
     */
    @Transactional
    public Long register(GoodsForm goodsForm)  {

        Goods goods = goodsRepository.save(goodsForm.toEntity());

        return goods.getId();
    }

    /**
     *
     * @param goodsId 상품ID
     * @return 해당 상품 ID와 일치하는 상품 정보
     */
    @Transactional
    public AdminGoods.AdminGoodsDetail goodsDetail(Long goodsId){

       AdminGoods.AdminGoodsDetail lists = goodsRepositoryCustom.findGoodsById(goodsId);

        System.out.println(lists.toString()+"!!!!!!!");
        return Optional.ofNullable(lists).orElseThrow(()->{
            throw new IllegalArgumentException("조회결과 없음");
        });

    }

    /**
     *
     * @param errors 입력 검증
     * @return 입력 검증 후 에러 메시지 출력
     */
    @Transactional
    public Map<String, String> validateHandling(Errors errors){
        Map<String, String> validatorResult = new HashMap<>();

        for(FieldError error : errors.getFieldErrors()){
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());

        }

        return validatorResult;
    }


    /**
     * 상품 정보 수정
     * @param goodsForm 수정된 정보가 담겨있는 입력form
     * @param file 수정된 상품 메인 사진
     * @param files 수정된 상품 상세 사진
     * @return 상품 정보 반환
     * @throws IOException 이미지 처리 예외처리
     */
    @Transactional
    public Goods modify(GoodsForm goodsForm, MultipartFile file, List<MultipartFile> files)
            throws IOException {

        //수정된 메인 사진이 있다면 기존 사진 삭제 후 수정된 사진으로 업데이트
        if(!file.isEmpty()) {

            //기존 사진 삭제
            fileService.removeMainImg(goodsForm.getId());

            //새로 수정된 사진 로컬 서버 저장 및 DB저장
            fileService.registerMainImg(file, goodsForm.getId());
        }else{
            System.out.println("입력된 메인 사진 없음");
        }

        //상세 사진
        if(!files.get(0).isEmpty()){
            //기존 사진 삭제
            fileService.removeDetailImgs(goodsForm.getId());

            //새로 수정된 사진 로컬 서버 저장 및 DB저장
            fileService.registerDetailImg(files, goodsForm.getId());
        }


        Goods goods = goodsRepository.findById(goodsForm.getId()).get();

        //상품 기본 내용 업데이트
        goods.update(goodsForm);
        return Optional.ofNullable(goods).orElseThrow(()->{
            throw new IllegalArgumentException("조회 정보 없음");
        });

    }


    /**
     * 상품 기본 정보와 상품 사진들이 삭제되는 로직이 담겨있음
     * @param goodsId 상품ID
     */
    @Transactional
    public void delete(Long goodsId){

        if (goodsId == null) {

            throw new IllegalArgumentException("유효하지 않은 번호");
        }


        fileService.removeMainImg(goodsId);
        fileService.removeDetailImgs(goodsId);
        goodsRepository.deleteById(goodsId);


    }


    /**
     * 관리자 페이지 상품 관련 문의 목록
     * @param pageable pageable 페이징 처리를 위한 객체
     * @param qnaState 해당 문의에 대한 관리자 답변 여부
     * @param cate 검색 카테고리
     * @param keyword 검색 키워드
     * @return
     */
    @Transactional
    public Page<AdminGoodsQnaListDto> getGoodsQnaList(Pageable pageable,String qnaState, String cate, String keyword){

        Page<AdminGoodsQnaListDto> qnaList = goodsRepositoryCustom.getQnaList(pageable, qnaState, cate, keyword);

        return qnaList;
    }


    /**
     * 상품 문의 상세보기
     * @param qnaId 상품문의ID
     * @return 해당 상품 정보와 문의 사항
     */
    @Transactional
    public Optional<AdminGoodsQueDetailDto> getGoodsQnaDetail(Long qnaId){

        if (qnaId == null) {

            throw new IllegalArgumentException("유효하지 않은 번호");
        }

        return goodsRepositoryCustom.getQnaDetail(qnaId);
    }


    /**
     *
     * @param goodsId 상품ID
     * @param pageable pageable
     * @param state 상품 관련 문의 답변 여부 확인
     * @return 해당 상품 관련 문의 사항 목록
     */
    @Transactional
    public Page<AdminGoodsQnaListDto> findGoodsDetailQnaList(Long goodsId, Pageable pageable, String state){

       return goodsRepositoryCustom.getQnaList(goodsId, pageable, state);

    }

    /**
     * 해당 상품과 관련한 상품 정보와 문의사항
     * @param goodsId 상품ID
     * @param pageable pageable
     * @param state 상품 관련 리뷰 답변 여부 확인
     * @return 해당 상품 관련 리뷰 목록
     */
    @Transactional
    public Page<AdminGoodsReview.AdminGoodsRelatedReview> findGoodsDetailReviewList(Long goodsId, Pageable pageable, String state){
        return goodsRepositoryCustom.getReviewList(goodsId,pageable,state);
    }


    /**
     * 해당 상품 문의에 관한 관리자 답변 등록
     * @param goodsQueReplyForm 관리자 답변에 관한 정보가 들어있는 form
     */
    @Transactional
    public void addQnaReply(GoodsQueReplyForm goodsQueReplyForm){

        Optional<GoodsQue> goodsQue = goodsQueRepository.findById(goodsQueReplyForm.getGoodsQueId());

        goodsQueReplyRepository.save(goodsQueReplyForm.toEntity());
        goodsQue.get().updateStateOn();

    }

    /**
     * 해당 상품 문의에 관한 관리자 답변
     * @param goodsQueId 상품문의ID
     * @return
     */
    @Transactional
    public AdminGoodsQueReplyDto replyList(Long goodsQueId){

        return goodsRepositoryCustom.getReplyList(goodsQueId);

    }


    /**
     * 해당 상품 리뷰에 관한 관리자 답변 수정
     * @param goodsQueReplyForm
     */
    @Transactional
    public void replyModify(GoodsQueReplyForm goodsQueReplyForm){

        GoodsQueReply goodsQueReply = goodsQueReplyRepository.findById(goodsQueReplyForm.getId()).get();
        goodsQueReply.update(goodsQueReplyForm);

    }

    /**
     * 해당 상품 리뷰에 관한 관리자 답변 삭제
     * @param replyId 상품 문의 답변 ID
     */
    @Transactional
    public void replyDelete(Long replyId){


        GoodsQue goodsQue = goodsQueRepository.findByGoodsQueReplyId(replyId).get();
        goodsQue.deleteState();

        goodsQueReplyRepository.deleteById(replyId);


    }





    /**
     * 관리자 페이지 상품 리뷰 목록
     * @param pageable 페이징처리를 위한 Pageable
     * @param searchReviewForm 검색 카테고리, 키워드
     * @return 상품 리뷰 목록
     */
    @Transactional
    public Page<AdminGoodsReview.AdminGoodsReviewList.AdminGoodsReviewResultList> reviewList(Pageable pageable, SearchReviewForm searchReviewForm){

        return adminGoodsRepositoryCustom.reviewList(pageable, searchReviewForm);
    }

    /**
     * 상품 기본 정보 및 리뷰 정보
     * @param orderReviewId 주문리뷰ID
     * @return 해당 주문 리뷰ID에 맞는 상품 기본 정보 및 리뷰 정보
     */
    @Transactional
    public AdminGoodsReview.AdminGoodsReviewDetail.AdminGoodsReviewResultDetail reviewDetail(Long orderReviewId){

        if (orderReviewId == null) {

            throw new IllegalArgumentException("유효하지 않은 번호");

        }

        return Optional.ofNullable(adminGoodsRepositoryCustom.goodsReviewDetail(orderReviewId))
                .orElseThrow(()->{throw new IllegalArgumentException("정보없음");});
    }


    /**
     * 상품 리뷰에 관리자 답변 등록
     * @param goodsReviewReplyForm 관리자 답변 정보가 담긴 form
     */
    @Transactional
    public void addGoodsReviewReply(GoodsReviewReplyForm goodsReviewReplyForm){

        //관리자 답변 저장
        adminGoodsReviewRepository.save(goodsReviewReplyForm.toEntity());

        //관리자 답변이 정상적으로 등록되면 해당 리뷰의 상태가 변경 - 답변 완료
        adminGoodsReviewRepository.updateOrderReviewState(goodsReviewReplyForm.getOrderReviewId());

    }

    /**
     * 해당 상품 리뷰에 등록된 관리자 답변 가져오기
     * @param orderReviewId 주문 리뷰ID
     * @return 해당 상품 리뷰에 등록된 관리자 답변
     */
    @Transactional
    public AdminGoodsReview.AdminGoodsReviewApply goodsReviewReplyList(Long orderReviewId){
        return adminGoodsRepositoryCustom.goodsReviewReplyList(orderReviewId);
    }


    /**
     * 해당 상품 리뷰에 등록된 관리자 답변 삭제
     * @param replyId 상품 리뷰답변ID
     * @param orderReviewId 주문 리뷰ID
     */
    @Transactional
    public void goodsReviewReplyDelete(Long replyId, Long orderReviewId){


        adminGoodsReviewRepository.deleteOrderReviewState(orderReviewId);
        adminGoodsReviewRepository.deleteById(replyId);

    }

    /**
     * 해당 상품 리뷰에 등록된 관리자 답변 수정
     * @param goodsReviewReplyForm 관리자 답변 정보가 담긴 form
     */
    @Transactional
    public void goodsReviewReplyModify(GoodsReviewReplyForm goodsReviewReplyForm){


        adminGoodsReviewRepository.updateReviewReply
                (goodsReviewReplyForm.getGoodsReviewReplyContent(),
                        goodsReviewReplyForm.getId());

    }
}
