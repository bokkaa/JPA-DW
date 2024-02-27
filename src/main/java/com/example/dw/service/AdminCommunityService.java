package com.example.dw.service;


import com.example.dw.domain.dto.admin.*;
import com.example.dw.domain.dto.community.WalkMateListDto;
import com.example.dw.domain.form.SearchCateLocationForm;
import com.example.dw.domain.form.SearchForm;
import com.example.dw.repository.admin.AdminCommunityRepositoryCustom;
import com.example.dw.repository.community.QuestionRepository;
import com.example.dw.repository.community.WalkingMateRepositoryCustom;
import com.example.dw.repository.freeBoard.FreeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommunityService {


    private final WalkingMateRepositoryCustom walkingMateRepositoryCustom;
    private final QuestionRepository questionRepository;
    private final FreeBoardRepository freeBoardRepository;
    private final AdminCommunityRepositoryCustom adminCommunityRepositoryCustom;


    /**
     * 관리자 페이지 - QnA 목록
     * @param pageable pageable 객체
     * @param searchForm 검색 정보가 담긴 form
     * @return
     */
    @Transactional
    public Page<AdminQnaBoardList> qnaBoardList(Pageable pageable, SearchForm searchForm){

        return adminCommunityRepositoryCustom.qnaBoardList(pageable, searchForm);
    }

    /**
     * 관리자 페이지 - Qna 상세
     * @param qnaId Qna 게시글 ID
     * @return 해당 Qna 게시글ID와 일치하는 게시글 정보
     */
    @Transactional
    public AdminQnaDetailResultDto qnaBoardDetail(Long qnaId){
        return adminCommunityRepositoryCustom.qnaDetail(qnaId);
    }

    /**
     * 관리자 페이지 - Qna 삭제
     * @param qnaId Qna 게시글 ID
     */
    @Transactional
    public void qnaDelete(Long qnaId){

        questionRepository.deleteById(qnaId);
    }

    /**
     * 관리자 페이지 - 자유게시판 목록
     * @param pageable pageable 객체
     * @param searchForm 검색 정보가 담긴 form
     * @return
     */
    @Transactional
    public Page<AdminFreeBoardList> freeBoardList(Pageable pageable, SearchForm searchForm){

        return adminCommunityRepositoryCustom.freeBoardList(pageable, searchForm);

    }

    /**
     * 관리자 페이지 - 자유게시판 상세
     * @param freeBoardId 자유게시판 ID
     * @return 해당 자유게시판 ID와 일치하는 게시글 정보
     */
    @Transactional
    public AdminFreeDetailResultDto freeBoardDetail(Long freeBoardId){
        return adminCommunityRepositoryCustom.freeBoardDetail(freeBoardId);
    }

    /**
     * 관리자 페이지 - 자유게시판 삭제
     * @param freeBoardId 자유게시판 ID
     */
    @Transactional
    public void freeBoardDelete(Long freeBoardId){

        freeBoardRepository.deleteById(freeBoardId);
    }


    /**
     * 관리자 페이지 - 산책메이트 게시글 목록
     * @param page page 변수
     * @param searchCateLocationForm 검색 정보
     * @return 산책메이트 게시글 목록
     */
    @Transactional
    public Page<WalkMateListDto> walkMateList(int page, SearchCateLocationForm searchCateLocationForm){


        Pageable pageable = PageRequest.of(page, 15);

        return walkingMateRepositoryCustom.findAllWalkMate(pageable, searchCateLocationForm);
    }

    /**
     * 관리자 페이지 - 산책메이트 게시물 상세 정보
     * @param walkMateId 산책메이트 게시물ID
     * @return 산책메이트 게시물 상세 정보
     */
    @Transactional
    public AdminWalkMateDetailDto walkMateDetailPage(Long walkMateId){

        if (walkMateId == null) {
            throw new IllegalArgumentException("유효하지 않은 게시번호");
        }

        return Optional.ofNullable(walkingMateRepositoryCustom.adminWalkMateDetail(walkMateId))
                .orElseThrow(()->{throw new IllegalArgumentException("조회정보 없음");});
    }

}
