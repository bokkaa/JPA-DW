package com.example.dw.domain.dto.community;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.community.QQuestionPopularityListDto is a Querydsl Projection type for QuestionPopularityListDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QQuestionPopularityListDto extends ConstructorExpression<QuestionPopularityListDto> {

    private static final long serialVersionUID = 1485797266L;

    public QQuestionPopularityListDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> questionTitle, com.querydsl.core.types.Expression<String> questionContent, com.querydsl.core.types.Expression<java.time.LocalDateTime> questionRd, com.querydsl.core.types.Expression<java.time.LocalDateTime> questionMd, com.querydsl.core.types.Expression<Long> questionViewCount, com.querydsl.core.types.Expression<Long> commentCount) {
        super(QuestionPopularityListDto.class, new Class<?>[]{long.class, String.class, String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, long.class, long.class}, id, questionTitle, questionContent, questionRd, questionMd, questionViewCount, commentCount);
    }

}

