package com.example.dw.domain.dto.community;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//freeBoardList 게시판 조회를 위한 DTO
@Data
@NoArgsConstructor
public class FreeBoardDto {

    private Long id;
    private String freeBoardTitle;
    private String freeBoardContent;
    private LocalDate freeBoardRd;
    private LocalDate freeBoardMd;
    private Long freeBoardViewCount;

    //자유게시판 이미지 추가
    private Long freeBoardImgId;
    private String freeBoardImgRoute;
    private String freeBoardImgName;
    private String freeBoardImgUuid;

    //유저 정보 추가
    private Long userId;  // User의 id 필드
    private String userAccount;  // User의 userAccount 필드
    private String userNickName;  // User의 userNickName 필드

    @QueryProjection
    public FreeBoardDto(Long id, String freeBoardTitle, String freeBoardContent,
                        LocalDate freeBoardRd, LocalDate freeBoardMd, Long freeBoardViewCount,
                        Long freeBoardImgId, String freeBoardImgRoute, String freeBoardImgName,
                        String freeBoardImgUuid, Long userId, String userAccount,
                        String userNickName) {
        this.id = id;
        this.freeBoardTitle = freeBoardTitle;
        this.freeBoardContent = freeBoardContent;
        this.freeBoardRd = freeBoardRd;
        this.freeBoardMd = freeBoardMd;
        this.freeBoardViewCount = freeBoardViewCount;

        //자유게시판 이미지 추가
        this.freeBoardImgId = freeBoardImgId;
        this.freeBoardImgRoute = freeBoardImgRoute;
        this.freeBoardImgName = freeBoardImgName;
        this.freeBoardImgUuid = freeBoardImgUuid;

        // 유저 정보 추가
        this.userId = userId;
        this.userAccount = userAccount;
        this.userNickName = userNickName;
    }
}
