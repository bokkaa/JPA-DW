package com.example.dw.controller;

import com.example.dw.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class UsersRestController {

    private final UsersService usersService;

    /**
     * 회원 계정 중복 검사
     * @param userAccount 희망하는 회원계정
     * @return 가입 존재 여부 True or False
     */
    @PostMapping("/users/account/check")
    public boolean checkAccountDuplication(@RequestParam(value = "userAccount") String userAccount) {
        if (userAccount == null) {
            throw new IllegalArgumentException("희망 이이디 입력 누락");
        }

        System.out.println("가입 시 입력한 희망 아이디 :" + userAccount);

        if (usersService.existsByUserAccount(userAccount) == true) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 회원 휴대전화 중복 검사
     * @param userPhone 입력된 휴대전화 번호
     * @return 가입 존재 여부 True or False
     */
    @PostMapping("/users/phone/check")
    public boolean checkPhoneDuplication(@RequestParam("userPhone") String userPhone){
        if (userPhone == null) {
            throw new IllegalArgumentException("폰 번혼 누락");

        }
        System.out.println("기입된 전화번호 : " + userPhone);
        if(usersService.existsByUserPhone(userPhone) == true ){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 회원 이메일 중복 검사
     * @param userEmail 입력된 이메일 주소
     * @return 가입 존재 여부 True or False
     */
    @PostMapping("/users/email/check")
    public boolean checkEmailDuplication(@RequestParam("userEmail") String userEmail){
        if (userEmail == null) {
            throw new IllegalArgumentException(("이메일 입력 누락"));
        }
        System.out.println("기입된 이메일 : " + userEmail);

        if(usersService.existsByUserEmail(userEmail)==true){
            System.out.println("중복된 이메일입니다.");
            return false;
        }else {
            return true;
        }
    }




}