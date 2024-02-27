package com.example.dw.service;

import com.example.dw.domain.dto.admin.*;
import com.example.dw.domain.entity.user.Users;
import com.example.dw.repository.user.UsersRepository;
import com.example.dw.repository.user.UsersRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminUserService {


    private final UsersRepository usersRepository;
    private final UsersRepositoryCustom usersRepositoryCustom;


    /**
     *
     * @param userId 회원ID
     * @return 회원 상세 정보
     */
    //회원 상세보기
    @Transactional
    public AdminUserDetailResultDto userDetail(Long userId){


        return usersRepositoryCustom.findByUserId(userId);
    }


    /**
     *
     * @param userId 회원ID
     * @param pageable Pageable
     * @return 해당 회원이 작성한 QnA 게시글
     */
    @Transactional
    public Page<AdminUserDetailQnaListDto> qnalist(Pageable pageable, Long userId){
        return usersRepositoryCustom.userDetailQnaList(pageable,userId);
    }

    /**
     *
     * @param userId 회원ID
     * @param pageable Pageable
     * @return 해당 회원이 작성한 자유게시판 게시글
     */       @Transactional
    public Page<AdminUserDetailFreeBoardListDto> freeBoardList(Pageable pageable, Long userId){
        return usersRepositoryCustom.userDetailFreeBoardList(pageable, userId);
    }

    /**
     *
     * @param userId 회원ID
     * @param pageable Pageable
     * @return 해당 회원이 작성한 산책메이트 게시글
     */
    @Transactional
    public Page<AdminUserDetailWalkMateDto> walkMateList(Pageable pageable, Long userId){
        return usersRepositoryCustom.userDetailWalkMateList(pageable, userId);
    }


    /**
     *
     * @param pageable Pageable
     * @param userId 회원ID
     * @return 해당 회원의 주문 내역 및 총 주문 합계 금액
     */
    @Transactional
    public Page<AdminUserDetailOrderResultWithTotalPriceDto> orderList(Pageable pageable, Long userId){
        return usersRepositoryCustom.userPaymentList(pageable,userId);
    }


    //회원 상태 수정
    @Transactional
    public void modifyUserStatus(Long userId){
        Users users = usersRepository.findById(userId).get();
        users.recoverUsersState();

    }


}
