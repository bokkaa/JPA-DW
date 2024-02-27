package com.example.dw.controller;


import com.example.dw.domain.dto.community.WalkDetailStateDto;
import com.example.dw.domain.dto.community.WalkMateDetailDto;
import com.example.dw.domain.dto.user.UserPetDto;
import com.example.dw.domain.form.WalkMateForm;
import com.example.dw.service.WalkingMateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/walk")
public class WalkBoardController {


    private final WalkingMateService walkingMateService;

    /**
     * 산책게시글 리스트 페이지
     * @return
     */
    @GetMapping("/walkList")
    public String walkList(){
        return "/community/walkingMateList";
    }


    /**
     * 산책글 작성 페이지 이동
     * @param model model 객체
     * @param session session 객체
     * @return
     */
    //산책글 작성 페이지 이동
    @GetMapping("/walkWrite")
    public String walkWrite(Model model, HttpSession session){

        List<UserPetDto> userPets = walkingMateService.getUserPets((Long)session.getAttribute("userId"));
        System.out.println(userPets.toString());
        model.addAttribute("pets", userPets);

        return "/community/walkingMateWrite";


    }


    /**
     * 산책글 작성 완료
     * @param walkMateForm 산책게시글 작성 정보
     * @param session session 객체
     * @return 산책게시글 리스트 페이지 이동
     */
    //산책글작성 완료
    @PostMapping("/registerWalkMate")
    public RedirectView registerWalk(WalkMateForm walkMateForm, HttpSession session){


        Long userId = (Long)session.getAttribute("userId");
        walkMateForm.setUserId(userId);
        System.out.println(walkMateForm.toString());



        walkingMateService.registerWalkingMate(walkMateForm);

        return new RedirectView("/walk/walkList");
    }


    /**
     * 산책글 상세보기
     * @param id 산책게시글 ID
     * @param model Model 객체
     * @param session session 객체
     * @return 산책게시글 ID에 해당하는 산책게시글 상세 정보
     */
    @GetMapping("/detail/{id}/{userId}")
    public String walkDetail(@PathVariable("id") Long id, Model model, HttpSession session){
        
        Optional<WalkMateDetailDto> detail =  walkingMateService.walkDetailPage(id);

        System.out.println("[ 산책글 상세 ] : " + detail.toString());

        detail.ifPresent( details -> model.addAttribute("detail", details));

        List<WalkDetailStateDto> applierPetInfo = walkingMateService.findApplierPetInfo(id);
        System.out.println("[ 산책글 승인받은 신청자 펫정보 ] : " + applierPetInfo);
        model.addAttribute("applierPetInfo", applierPetInfo);
        
        //신청자 pet 목록
        
        try{
            Long sessionUserId = (Long)session.getAttribute("userId");

            //로그인 상태일 시
            if(sessionUserId != null){
                //신청자 펫리스트
                List<UserPetDto> petList = walkingMateService.getUserPets(sessionUserId);
                model.addAttribute("sessionUserPet", petList);

                //신청상태 확인
                Long applyUserId = walkingMateService.applyState(sessionUserId, detail.get().getId());
                model.addAttribute("replyAuthorized", applyUserId);
            }

        }catch (Exception e){
            //비로그인 상태면 예외처리
            e.printStackTrace();
        }


        return "/community/walkingMateDetail";
    }

    /**
     * 산책게시글 수정 페이지 이동
     * @param id 산책게시글 ID
     * @param model model 객체
     * @param session session 객체
     * @return 해당 산책게시글 ID에 맞는 게시글 수정 페이지 이동
     */
    //산책글 수정페이지 이동
    @GetMapping("/modify/{id}")
    public String walkModify(@PathVariable("id") Long id,
                        Model model, HttpSession session){

        Long userId = (Long)session.getAttribute("userId");
        List<UserPetDto> userPets = walkingMateService.getUserPets(userId);
        System.out.println(userPets.toString());
        model.addAttribute("pets", userPets);



        Optional<WalkMateDetailDto> detail =  walkingMateService.walkDetailPage(id);
        detail.ifPresent( details -> model.addAttribute("detail", details));
        System.out.println(detail.toString());

        return "/community/walkingMateModify";

    }


    /**
     * 산책글 수정 완료
     * @param walkMateForm 산책게시글 수정 정보
     * @return 산책게시글 리스트 페이지 이동
     */
    //산책글 수정 완료
    @PostMapping("/modifyOk/{id}")
    public RedirectView walkModifyOk(WalkMateForm walkMateForm){

        System.out.println("[ 산책글 수정 사항 ] : "+walkMateForm.toString());
        walkingMateService.walkModify(walkMateForm);

        return new RedirectView("/walk/walkList");

    }

    /**
     * 산책게시글 삭제
     * @param walkBoardId 산책게시글 ID
     * @return 산책게시글 리스트 페이지 이동
     */
    @GetMapping("/deleteWalkMate/{walkBoardId}")
    public RedirectView deleteWalkMate(@PathVariable("walkBoardId") Long walkBoardId){


        walkingMateService.walkDelete(walkBoardId);

        return new RedirectView("/walk/walkList");
    }


    /**
     * 산책메이트 신청 철회
     * @param walkMateId 산책게시글 ID
     * @param userId 유저ID
     * @return 산책게시글ID와 유저ID에 해당하는 산책게시글 상세 페이지 이동
     */
    @GetMapping("/applyCancel/{walkMateId}/{userId}")
    public RedirectView applyCancel(
            @PathVariable("walkMateId") Long walkMateId,
            @PathVariable("userId") Long userId
    ){
        walkingMateService.applyCanCel(walkMateId, userId);

        return new RedirectView("/walk/detail/"+walkMateId +"/"+userId);

    }


}
