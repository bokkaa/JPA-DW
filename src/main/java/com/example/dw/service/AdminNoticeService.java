package com.example.dw.service;


import com.example.dw.domain.dto.admin.AdminDto;
import com.example.dw.domain.entity.admin.Admin;
import com.example.dw.domain.entity.admin.FaqBoard;
import com.example.dw.domain.entity.admin.NoticeBoard;
import com.example.dw.domain.form.FaqBoardForm;
import com.example.dw.domain.form.NoticeBoardForm;
import com.example.dw.repository.admin.AdminRepository;
import com.example.dw.repository.admin.FaqBoardRepository;
import com.example.dw.repository.admin.NoticeBoardRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNoticeService {

    private final AdminRepository adminRepository;
    private final NoticeBoardRepository noticeBoardRepository;
    private final FaqBoardRepository faqBoardRepository;



    //관리자 로그인
    @Transactional
    public AdminDto adminLogin(String adminAccount, String adminPassword){

       Admin admin= adminRepository.findByAdminAccountAndAdminPassword(adminAccount, adminPassword).get();

       AdminDto adminDto = new AdminDto(admin.getId(), admin.getAdminAccount(), admin.getAdminPassword());

        return adminDto;

    }
    //관리자 로그아웃
    @Transactional
    public void adminLogout(HttpSession session){
        session.invalidate();
    }


    /**
     * FAQ 등록
     * @param faqBoardForm 작성 정보가 담겨져 있는 form
     * @return 성공적으로 등록 시 해당 게시물의 ID값 반환
     */
    @Transactional
    public Long faqRegister(FaqBoardForm faqBoardForm){

        FaqBoard faqBoard = faqBoardRepository.save(faqBoardForm.toEntity());

        return faqBoard.getId();

    }


    /**
     * FAQ 상세보기
     * @param faqBoardId FAQ ID
     * @return FAQ 상세
     */
    @Transactional
    public FaqBoard faqDetail(Long faqBoardId){

        if (faqBoardId == null) {
            throw new IllegalArgumentException("FAQ 번호 누락");
        }

        FaqBoard detail = faqBoardRepository.findById(faqBoardId).get();
        return Optional.ofNullable(detail).orElseThrow(()->{throw new IllegalArgumentException("존재하지 않음");});
    }

    /**
     * FAQ 수정
     * @param faqBoardForm 수정 정보가 담긴 form
     * @param id FAQ ID
     * @return
     */
    @Transactional
    public FaqBoard faqModify(FaqBoardForm faqBoardForm, Long id) {

        if (id == null) {
            throw new IllegalArgumentException("FAQ 번호 누락");
        }

        FaqBoard faqBoard = faqBoardRepository.findById(id).get();
        faqBoard.update(faqBoardForm);

        return Optional.ofNullable(faqBoard).orElseThrow(()->{throw new IllegalArgumentException("존재하지 않음");});
    }

    /**
     * FAQ 삭제
     * @param id FAQ ID
     */
    @Transactional
    public void faqDelete(Long id){

        if (id == null) {
            throw new IllegalArgumentException("faq 게시번호 누락");
        }

        faqBoardRepository.deleteById(id);

    }

    /**
     * 공지사항 작성
     * @param noticeBoardForm 작성 정보가 담겨져있는 form
     * @return 성공적으로 등록 시 해당 공지사항 ID값 반환
     */
    @Transactional
    public Long register(NoticeBoardForm noticeBoardForm){

        NoticeBoard noticeBoard = noticeBoardRepository.save(noticeBoardForm.toEntity());

        return noticeBoard.getId();

    }

    /**
     * 공지사항 상세보기
     * @param id 공지사항 ID
     * @return
     */
    @Transactional
    public NoticeBoard noticeDetail(Long id){

        if (id == null) {
            throw new IllegalArgumentException("공지사항 번호 누락");
        }
        NoticeBoard detail = noticeBoardRepository.findById(id).get();

        return Optional.ofNullable(detail).orElseThrow(()->{
            throw new IllegalArgumentException("존재하지 않음");
        });
    }

    /**
     * 공지사항 수정
     * @param noticeBoardForm 수정 정보가 담겨져 있는 form
     * @param id 공지사항 ID
     * @return
     */
    @Transactional
    public NoticeBoard noticeModify(NoticeBoardForm noticeBoardForm,Long id){

        if (id == null) {
            throw new IllegalArgumentException("공지사항 번호 누락");
        }

        //영속상태로
        NoticeBoard noticeBoard = noticeBoardRepository.findById(id).get();

        //변경감지
        noticeBoard.update(noticeBoardForm);

        return Optional.ofNullable(noticeBoard).orElseThrow(()->{
            throw new IllegalArgumentException("존재하지 않음");
        });
    }

    /**
     * 공지사항 삭제
     * @param id 공지사항 ID
     */
    @Transactional
    public void noticeDelete(Long id ){

        if (id == null) {

            throw new IllegalArgumentException("존재하지 않는 공지사항 게시 번호");
        }

        noticeBoardRepository.deleteById(id);
    }



}
