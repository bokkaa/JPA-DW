package com.example.dw.api;

import com.example.dw.domain.dto.admin.*;
import com.example.dw.repository.user.UsersRepositoryCustom;
import com.example.dw.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/*")
public class AdminUserApiController {

    private final AdminUserService adminUserService;
    private final UsersRepositoryCustom usersRepositoryCustom;

    /**
     *
     * @param page 페이징 처리를 위한 페이지 변수
     * @param userState 회원 가입 상태(가입 중 / 탈퇴 상태)
     * @param cate 아아디, 이름, 이메일 등 검색 카테고리
     * @param keyword 검색 키워드
     * @return 가입 회원 목록 및 탈퇴 회원 목록
     */
    @GetMapping("/userList/{page}")
    public Page<UserListDto> findUserList(
            @PathVariable("page") int page,
            String userState,
            String cate, String keyword
    ){

        Pageable pageable = PageRequest.of(page, 15);

        return usersRepositoryCustom.findByAll(pageable, cate, keyword, userState);

    }


    //회원상세(통신확인용)
    @GetMapping("/userDetail/{userId}")
    public AdminUserDetailResultDto userDetail(
            @PathVariable("userId") Long userId
    ){

        return usersRepositoryCustom.findByUserId(userId);
    }


    /**
     *
     * @param userId 회원ID
     * @param page 페이지 변수
     * @return 해당 회원이 작성한 QnA 게시글
     */
    @GetMapping("/userQnaList/{userId}/{page}")
    public Page<AdminUserDetailQnaListDto> qnaList(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ){

        Pageable pageable = PageRequest.of(page, 5);



        return adminUserService.qnalist(pageable, userId);
    }

    /**
     *
     * @param userId 회원ID
     * @param page 페이지 변수
     * @return 해당 회원이 작성한 자유게시판 게시글
     */
    @GetMapping("/userFreeBoardList/{userId}/{page}")
    public Page<AdminUserDetailFreeBoardListDto> freeBoardList(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ){
        Pageable pageable = PageRequest.of(page, 5);

        return adminUserService.freeBoardList(pageable, userId);

    }


    /**
     *
     * @param userId 회원ID
     * @param page 페이지 변수
     * @return 해당 회원이 작성한 산책메이트 게시글
     */    @GetMapping("/userWalkList/{userId}/{page}")
    public Page<AdminUserDetailWalkMateDto> walkList(
            @PathVariable("userId") Long userId,
            @PathVariable("page") int page
    ){

        Pageable pageable = PageRequest.of(page, 5);

        return adminUserService.walkMateList(pageable, userId);

    }


    /**
     *
     * @param userId 회원ID
     * @param page 페이지 변수
     * @return 해당 회원의 주문 내역 및 총 주문 합계 금액
     */
    @GetMapping("/userOrderList/{userId}/{page}")
    public Page<AdminUserDetailOrderResultWithTotalPriceDto> orderList(
            @PathVariable("userId") Long userId,
            @PathVariable("page")int page){

        Pageable pageable = PageRequest.of(page, 5);

        return adminUserService.orderList(pageable,userId);
    }


    /**
     * @return 일별 회원 가입 수
     */
    @GetMapping("/daily")
    public List<AdminUserChartDto> getDaily() {

        return usersRepositoryCustom.findJoinCountByAll();
    }

    /**
     * @return 회원 신규 가입 최근 4개
     * */
    @GetMapping("/newUserStatus")
    public Map<String, List> status(){

        Map<String, List> status = usersRepositoryCustom.newUserStatus();

        return status;

    }
}
