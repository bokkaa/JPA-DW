package com.example.dw.api;


import com.example.dw.domain.dto.community.WalkMateDetailReplyDto;
import com.example.dw.domain.dto.community.WalkMateListDto;
import com.example.dw.domain.form.SearchLocationForm;
import com.example.dw.domain.form.WalkingMateCommentForm;
import com.example.dw.domain.form.WalkingMateStateForm;
import com.example.dw.service.WalkingMateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/walks/*")
@RequiredArgsConstructor
public class WalkBoardApiController {


    private final WalkingMateService walkingMateService;


    @Value("${file.user}")
    private String userImg;

    
    /**
     * 로그인 세션 상태 확인
     * @param session session 객체
     * @return 회원 ID
     */
    @PostMapping("/sessionOk")
    public Long checkSession(HttpSession session){

        return (Long)session.getAttribute("userId");
    }

    /**
     * 산책게시글 리스트
     * @param page page 변수
     * @param searchLocationForm 검색정보가 담긴 from
     * @return 산책게시글 리스트
     */
    @GetMapping("/walkList/{page}")
    public Page<WalkMateListDto> getWalkList(@PathVariable("page") int page,
                                             SearchLocationForm searchLocationForm){

        System.out.println(searchLocationForm.toString());
        return walkingMateService.walkMateList(page, searchLocationForm);
    }

    /**
     * 산책게시글 작성제한(희망 요일 1회 작성 가능)
     * @param userId 회원ID
     * @param walkingMateDate 희망 신청일짜
     * @return 작성여부 확인(0 or 1)
     */
    @PostMapping("/limitCheck/{userId}")
    public Integer limitCheck(@PathVariable("userId") Long userId,
                           String walkingMateDate){

        System.out.println(walkingMateDate+"!@#@!#!@#!@");
        return walkingMateService.limitWriteByDay(userId, walkingMateDate);

    }



    /**
     * 산책게시글 댓글 리스트
     * @param walkBoardId 산책게시글ID
     * @return 해당 산책게시글ID에 등록된 댓글 리스트
     */
    @GetMapping("/showReplyList/{walkBoardId}")
    public List<WalkMateDetailReplyDto> showReplyList(
            @PathVariable("walkBoardId") Long walkBoardId
    ){

        return walkingMateService.getReplyList(walkBoardId);

    }

    /**
     * 산책게시글 댓글 등록
     * @param walkingMateCommentForm 산책게시글 댓글정보가 담긴 form
     */
    @PostMapping("/detailReply")
    public void registerReply(WalkingMateCommentForm walkingMateCommentForm){

        walkingMateService.walkDetailReply(walkingMateCommentForm);

    }


    /**
     * 산책게시글 댓글 회원 사진
     * @param userImgPath 회원 사진 ID
     * @return 회원 사진 경로
     * @throws IOException
     */
    @GetMapping("/walkDetailImg")
    public byte[] getEmpImg(String userImgPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(userImg, userImgPath));
    }


    /**
     * 산책게시글 댓글 수정
     * @param walkingMateCommentForm 댓글 수정 정보가 담긴 form
     */
    @PatchMapping("/walkReplyModify")
    public void modifyReply(WalkingMateCommentForm walkingMateCommentForm){

        System.out.println(walkingMateCommentForm.toString());

        walkingMateService.modifyReply(walkingMateCommentForm);

    }

    /**
     * 산책게시글 댓글 삭제
     * @param walkCommentId 산책게시글 댓글ID
     */
    @DeleteMapping("/walkReplyDelete/{walkCommentId}")
    public void deleteReply(@PathVariable("walkCommentId") Long walkCommentId){

        walkingMateService.deleteReply(walkCommentId);
    }


    /**
     * 산책메이트 신청 중복검사
     * @param walkMateId 산책게시글ID
     * @param session session 객체
     * @return 신청 정보 (0 or 1)
     */
    @GetMapping("/applyCheck/{walkMateId}")
    public Long applyCheck(@PathVariable("walkMateId") Long walkMateId, HttpSession session){

            Long sessionUserId = (Long)session.getAttribute("userId");

            return walkingMateService.applyCheck(walkMateId, sessionUserId);
    }


    /**
     * 산책메이트 신청
     * @param walkingMateStateForm 신청정보가 담긴 form
     */
    @GetMapping("/applyWalkMate")
    public void applyWalkMate(WalkingMateStateForm walkingMateStateForm){


        walkingMateService.applyWalkMate(walkingMateStateForm);

    }

//    //신청자 펫 정보 //post맨 확인용
//    @GetMapping("/applierPetInfo/{walkMateId}")
//    public List<WalkDetailStateDto> applierPetInfo(
//            @PathVariable("walkMateId") Long walkMateId){
//
//        return walkingMateService.findApplierPetInfo(walkMateId);
//
//    }


}
