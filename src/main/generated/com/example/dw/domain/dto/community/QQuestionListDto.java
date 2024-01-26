package com.example.dw.domain.dto.community;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.community.QQuestionListDto is a Querydsl Projection type for QuestionListDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QQuestionListDto extends ConstructorExpression<QuestionListDto> {

    private static final long serialVersionUID = -943335417L;

    public QQuestionListDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> questionTitle, com.querydsl.core.types.Expression<String> questionContent, com.querydsl.core.types.Expression<java.time.LocalDateTime> questionRd, com.querydsl.core.types.Expression<java.time.LocalDateTime> questionMd, com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> userAccount, com.querydsl.core.types.Expression<String> userNickName, com.querydsl.core.types.Expression<Long> commentCount, com.querydsl.core.types.Expression<Long> userFileId, com.querydsl.core.types.Expression<String> route, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> uuid, com.querydsl.core.types.Expression<Long> questionImgId, com.querydsl.core.types.Expression<String> questionImgRoute, com.querydsl.core.types.Expression<String> questionImgUuid, com.querydsl.core.types.Expression<String> questionImgName) {
        super(QuestionListDto.class, new Class<?>[]{long.class, String.class, String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, long.class, String.class, String.class, long.class, long.class, String.class, String.class, String.class, long.class, String.class, String.class, String.class}, id, questionTitle, questionContent, questionRd, questionMd, userId, userAccount, userNickName, commentCount, userFileId, route, name, uuid, questionImgId, questionImgRoute, questionImgUuid, questionImgName);
    }

}

