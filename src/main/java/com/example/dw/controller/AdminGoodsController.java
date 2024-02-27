package com.example.dw.controller;

import com.example.dw.domain.dto.admin.AdminGoodsQueDetailDto;
import com.example.dw.domain.dto.admin.goods.AdminGoods;
import com.example.dw.domain.dto.admin.goods.AdminGoodsReview;
import com.example.dw.domain.form.GoodsForm;
import com.example.dw.service.AdminGoodsService;
import com.example.dw.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminGoodsController {

    private final AdminGoodsService adminGoodsService;
    private final FileService fileService;

    //상품리스트
    @GetMapping("/goodsList")
    public String GoodsList(){
        return "/admin/AdminGoodsList";
    }

    //상품등록 페이지 이동
    @GetMapping("/goodsReg")
    public String goodsRegisterPage(){
        return "/admin/adminGoodsReg";
    }


    /**
     *
     * @param goodsForm 입력된 상품 기본 정보
     * @param file 입력된 상품 메인 사진 1장
     * @param files 입력된 상품 상세 사진 최대 4장
     * @param errors 입력 검증
     * @param model html로 내보내기위한 model
     * @return 상품 리스트 페이지 이동
     * @throws IOException 사진 파일 저장 관련 예외처리
     */
    @PostMapping("/goodsRegister")
    public String goodsRegister(@Valid GoodsForm goodsForm,
                                @RequestParam("goodsMainImg") MultipartFile file,   //메인사진
                                @RequestParam("goodsDetailImg") List<MultipartFile> files //상세사진
                                    , Errors errors, Model model) throws IOException{

        if(errors.hasErrors()){
            model.addAttribute("error", goodsForm);

            Map<String, String> validatorResult = adminGoodsService.validateHandling(errors);
            for(String key : validatorResult.keySet()){
                model.addAttribute(key, validatorResult.get(key));

            }
            return "/admin/adminGoodsReg";
        }

        //해당 컨트롤러로 타고 들어오는 정보 내용
        System.out.println("[상품 등록 정보 ] : " + goodsForm.toString());
        System.out.println("[상품 메인 사진 정보] : " + file.getOriginalFilename());
        files.forEach(r-> System.out.println("[상품 상세 사진 정보] : " + r.getOriginalFilename()));


        //상품 기본 정보 등록
        //등록 시 반환되는 goods_id값을 이용하여 사진 등록 메소드의 매개변수로 사용
        Long id = adminGoodsService.register(goodsForm);

        fileService.registerMainImg(file, id);      //메인사진
        fileService.registerDetailImg(files, id);   //상세사진

        return ("redirect:/admin/goodsList");
    }

    /**
     *
     * @param goodsId 상품ID
     * @param model html로 내보내기 위한 model
     * @return 해당 상품 ID와 일치하는 상품 정보
     */
    @GetMapping("/detail/{goodsId}")
    public String goodsDetail(@PathVariable("goodsId") Long goodsId, Model model){


        AdminGoods.AdminGoodsDetail detail = adminGoodsService.goodsDetail(goodsId);


        System.out.println("[상품 상세 정보] : "+detail.toString());
        model.addAttribute("detail", detail);


        return "/admin/adminGoodsDetail";

    }


    /**
     *
     * @param goodsId 상품ID
     * @param model 기존 상품 정보를 보여주기 위해 사용하는 model
     * @return 해당 상품Id에 맞는 상품 정보
     */
    @GetMapping("/modify/{goodsId}")
    public String goodsModifyPage(@PathVariable("goodsId") Long goodsId, Model model){

        AdminGoods.AdminGoodsDetail detail = adminGoodsService.goodsDetail(goodsId);

        model.addAttribute("detail", detail);

        return "/admin/adminGoodsModify";
    }

    /**
     *
     * @param goodsId 상품ID
     * @param goodsForm 수정된 입력사항이 저장되는 form
     * @param file 수정된 상품 메인 사진
     * @param files 수정된 상품 상세 사진
     * @return 해당 상품 상세 페이지로 이동
     * @throws IOException 이미지 예외처리
     */
    @PutMapping("/modify/{goodsId}/edit")
    public RedirectView goodsModify(@PathVariable("goodsId") Long goodsId,
                                    GoodsForm goodsForm,
                                    @RequestParam("goodsMainImg") MultipartFile file,
                                    @RequestParam("goodsDetailImg") List<MultipartFile> files) throws IOException {

        goodsForm.setId(goodsId);
        System.out.println("받아오는 상품번호 : "+goodsForm.getId());


        adminGoodsService.modify(goodsForm,file,files);

        return new RedirectView("/admin/detail/" + goodsId);
    }


    /**
     * 상품 삭제
     * @param goodsId 상품ID
     * @return 삭제 성공 시 상품리스트 페이지 이동
     */
    @GetMapping("/delete/{goodsId}")
    public RedirectView goodsDelete(@PathVariable("goodsId") Long goodsId){


        adminGoodsService.delete(goodsId);

        return new RedirectView("/admin/goodsList");
    }

    //상품 문의 리스트
    @GetMapping("/goodsQna")
    public String goodsQna(){
        return "admin/adminGoodsQue";
    }


    /**
     * 상품 문의 상세보기
     * @param qnaId 상품문의ID
     * @param model 상품문의 정보를 html로 내보내기 위한 model
     * @return 해당 상품 상세 페이지 이동
     */
    @GetMapping("/qnaDetail/{qnaId}")
    public String qnaDetail(@PathVariable("qnaId") Long qnaId,
                            Model model){

        Optional<AdminGoodsQueDetailDto> detail = adminGoodsService.getGoodsQnaDetail(qnaId);

        System.out.println(detail.get().toString());

        detail.ifPresent(details -> model.addAttribute("details", details));

        return "/admin/adminGoodsQueDetail";

    }


    //상품 리뷰 리스트
    @GetMapping("/goodsReview")
    public String goodsReview(){

        return "admin/adminGoodsReview";
    }


    /**
     * 상품 기본 정보 및 리뷰 정보
     * @param orderReviewId 주문리뷰ID
     * @return 해당 주문리뷰ID와 일치하는 상품 기본 정보 및 리뷰 정보
     */
    @GetMapping("/goodsReviewDetail/{orderReviewId}")
    public String goodsReviewDetail(@PathVariable("orderReviewId") Long orderReviewId, Model model){

        AdminGoodsReview.AdminGoodsReviewDetail.AdminGoodsReviewResultDetail detail = adminGoodsService.reviewDetail(orderReviewId);
        model.addAttribute("detail", detail);

        return "/admin/adminGoodsReviewDetail";
    }


}
