package com.example.dw.domain.dto.community;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionListDto {

    private Long id;
    private String questionTitle;
    private String questionContent;
    private LocalDateTime questionRd;
    private LocalDateTime questionMd;
    //조회수

    //유저정보 추가
    private Long userId;
    private String userAccount;
    private String userNickName;


    private Long commentCount =0L;

    private Long userFileId;
    private String route;
    private String name;
    private String uuid;



    //사용자 이미지
    private Long questionImgId;
    private String questionImgRoute;
    private String questionImgUuid;
    private String questionImgName;


    @QueryProjection
    public QuestionListDto(Long id, String questionTitle, String questionContent, LocalDateTime questionRd, LocalDateTime questionMd, Long userId, String userAccount, String userNickName, Long commentCount, Long userFileId, String route, String name, String uuid, Long questionImgId, String questionImgRoute, String questionImgUuid, String questionImgName) {
        this.id = id;
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questionRd = questionRd;
        this.questionMd = questionMd;
        this.userId = userId;
        this.userAccount = userAccount;
        this.userNickName = userNickName;
        this.commentCount = commentCount;
        this.userFileId = userFileId;
        this.route = route;
        this.name = name;
        this.uuid = uuid;
        this.questionImgId = questionImgId;
        this.questionImgRoute = questionImgRoute;
        this.questionImgUuid = questionImgUuid;
        this.questionImgName = questionImgName;
    }
}
