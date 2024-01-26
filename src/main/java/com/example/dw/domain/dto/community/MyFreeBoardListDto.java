package com.example.dw.domain.dto.community;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyFreeBoardListDto {

    private Long id;
    private String freeBoardTitle;
    private String freeBoardContent;
    private Long userId;
    private String userAccount;
    private String userNickName;

    private Long userFileId;
    private String route;
    private String uuid;
    private String name;

    private Long commentCount;

    private Long freeBoardImgId;
    private String freeBoardImgRoute;
    private String freeBoardImgName;
    private String freeBoardImgUuid;

    @QueryProjection

    public MyFreeBoardListDto(Long id, String freeBoardTitle, String freeBoardContent, Long userId, String userAccount, String userNickName, Long userFileId, String route, String uuid, String name, Long commentCount, Long freeBoardImgId, String freeBoardImgRoute, String freeBoardImgName, String freeBoardImgUuid) {
        this.id = id;
        this.freeBoardTitle = freeBoardTitle;
        this.freeBoardContent = freeBoardContent;
        this.userId = userId;
        this.userAccount = userAccount;
        this.userNickName = userNickName;
        this.userFileId = userFileId;
        this.route = route;
        this.uuid = uuid;
        this.name = name;
        this.commentCount = commentCount;
        this.freeBoardImgId = freeBoardImgId;
        this.freeBoardImgRoute = freeBoardImgRoute;
        this.freeBoardImgName = freeBoardImgName;
        this.freeBoardImgUuid = freeBoardImgUuid;
    }
}