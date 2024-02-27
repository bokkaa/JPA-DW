package com.example.dw.api;

import com.example.dw.domain.dto.admin.AdminNoticeBoardDto;
import com.example.dw.domain.dto.admin.AdminFaqBoardDto;
import com.example.dw.domain.form.SearchForm;
import com.example.dw.repository.admin.FaqBoardRepositoryCustom;
import com.example.dw.repository.admin.NoticeBoardRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admins/*")
public class AdminNoticeApiController {

    private final FaqBoardRepositoryCustom faqBoardRepositoryCustom;
    private final NoticeBoardRepositoryCustom noticeBoardRepositoryCustom;

    /**
     * faq 게시글 목록
     * @param page page 변수
     * @param searchForm 검색정보가 담긴 form
     * @return
     */
    @GetMapping("/faqList/{page}")
    public Page<AdminFaqBoardDto> findFaqList(
            @PathVariable("page") int page,
            SearchForm searchForm){

        Pageable pageable = PageRequest.of(page, 5);

        Page<AdminFaqBoardDto> result = faqBoardRepositoryCustom.findFaqListBySearch(pageable, searchForm);

        System.out.println(result.toString());

        return result;
    }

    /**
     * 공지사항 목록
     * @param page page 변수
     * @param searchForm 검색 정보가 담긴 form
     * @return
     */
    @GetMapping("/noticeList/{page}")
    public Page<AdminNoticeBoardDto> findNoticeList(
            @PathVariable("page") int page,
            SearchForm searchForm
    ){
        Pageable pageable = PageRequest.of(page, 10);

        Page<AdminNoticeBoardDto> result = noticeBoardRepositoryCustom.findNoticeListBySearch(pageable,searchForm);

        return result;
    }







}
