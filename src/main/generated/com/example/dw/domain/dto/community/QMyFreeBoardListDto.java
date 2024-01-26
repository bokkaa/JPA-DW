package com.example.dw.domain.dto.community;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.community.QMyFreeBoardListDto is a Querydsl Projection type for MyFreeBoardListDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMyFreeBoardListDto extends ConstructorExpression<MyFreeBoardListDto> {

    private static final long serialVersionUID = -1205564665L;

    public QMyFreeBoardListDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> freeBoardTitle, com.querydsl.core.types.Expression<String> freeBoardContent, com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> userAccount, com.querydsl.core.types.Expression<String> userNickName, com.querydsl.core.types.Expression<Long> userFileId, com.querydsl.core.types.Expression<String> route, com.querydsl.core.types.Expression<String> uuid, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<Long> commentCount, com.querydsl.core.types.Expression<Long> freeBoardImgId, com.querydsl.core.types.Expression<String> freeBoardImgRoute, com.querydsl.core.types.Expression<String> freeBoardImgName, com.querydsl.core.types.Expression<String> freeBoardImgUuid) {
        super(MyFreeBoardListDto.class, new Class<?>[]{long.class, String.class, String.class, long.class, String.class, String.class, long.class, String.class, String.class, String.class, long.class, long.class, String.class, String.class, String.class}, id, freeBoardTitle, freeBoardContent, userId, userAccount, userNickName, userFileId, route, uuid, name, commentCount, freeBoardImgId, freeBoardImgRoute, freeBoardImgName, freeBoardImgUuid);
    }

}

