package com.example.dw.controller;

import com.example.dw.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail/*")
public class MailController {

    private final MailService mailService;


    /**
     * 이메일 주소
     * @param mail 기입된 이메일 주소
     * @return 기입된 이메일 주소로 인증번호 전송
     */
    @ResponseBody
    @PostMapping("/mail")
    public String mailSend(String mail){

        int number = mailService.sendMail(mail);

        return number +"";
    }

    /**
     * 계정 찾기 정보 입력
     * @param userName 입력된 회원 이름
     * @param userPhone 입력된 회원 휴대전화 번호
     * @param userEmail 입력된 회원 이메일 주소
     * @return 해당 이메일 주소로 회원 계정 정보 전송
     */
    @ResponseBody
    @PostMapping("/userAccountCheck")
    public String accountFind(String userName, String userPhone, String userEmail){

        int number = mailService.reworkAccountMail(userName, userEmail, userPhone);

        String num = "" + number;

                return num;

    }

    /**
     * 비밀번호 찾기 정보 입력
     * @param userName 입력된 회원 이름
     * @param userAccount 입력된 회원 휴대전화 번호
     * @param userEmail 입력된 회원 이메일 주소
     * @return 해당 이메일 주소로 임시 비밀번호 전송
     */
    @ResponseBody
    @PostMapping("/userPassword")
    public String passwordFind(String userName, String userAccount, String userEmail){


        int number =mailService.reworkPwMail(userName, userAccount, userEmail);

        return number + "";

    }


}
