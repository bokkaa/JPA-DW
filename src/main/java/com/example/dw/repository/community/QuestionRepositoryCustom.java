package com.example.dw.repository.community;

import com.example.dw.domain.dto.community.*;
import com.example.dw.domain.entity.question.QuestionImg;
import com.example.dw.domain.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionRepositoryCustom {
    Page<QuestionListDto> findQnaListBySearch(Pageable pageable,SearchForm searchForm);

//    // detail page 조회
    List<QuestionDetailResultDto> findQnaById(Long id);

    List<QuestionImgDto> findAllByQuestionId(Long questionId);

    // 마이페이지 특정유거 작성한 질문 받아오기
    Page<QuestionListDto> findQnaListById(Pageable pageable,Long userId);

    List<QuestionPopularityListDto> findAllByQuestion();
}
