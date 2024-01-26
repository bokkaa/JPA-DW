package com.example.dw.domain.dto.community;


import com.example.dw.domain.dto.admin.UserFileDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyFreeBoardResultDto {

    private Long id;
    private String freeBoardTitle;
    private String freeBoardContent;
    private Long userId;
    private String userAccount;
    private String userNickName;

    private UserFileDto userFileDtos;

    private Long commentCount;

    private FreeBoardImgDto freeBoardImgDtos;



    public MyFreeBoardResultDto(Long id, String freeBoardTitle, String freeBoardContent, Long userId, String userAccount, String userNickName, UserFileDto userFileDtos, Long commentCount, FreeBoardImgDto freeBoardImgDtos) {
        this.id = id;
        this.freeBoardTitle = freeBoardTitle;
        this.freeBoardContent = freeBoardContent;
        this.userId = userId;
        this.userAccount = userAccount;
        this.userNickName = userNickName;
        this.userFileDtos = userFileDtos;
        this.commentCount = commentCount;
        this.freeBoardImgDtos = freeBoardImgDtos;
    }


    public MyFreeBoardResultDto setUserFileImg(UserFileDto userImg) {
        this.userFileDtos = userImg;
        return this;
    }

    public MyFreeBoardResultDto setFreeImg(FreeBoardImgDto freedImg) {
        this.freeBoardImgDtos = freedImg;
        return this;
    }
}