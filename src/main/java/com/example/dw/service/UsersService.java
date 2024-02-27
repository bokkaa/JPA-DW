package com.example.dw.service;


import com.example.dw.domain.form.JoinForm;

import com.example.dw.domain.entity.user.Users;
import com.example.dw.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UsersService {


    private final UsersRepository usersRepository;


    /**
     * 회원 계정 중복 검사
     * @param userAccount 희망하는 회원계정
     * @return 가입 존재 여부 True or False
     */
    @Transactional(readOnly = true)
    public boolean existsByUserAccount(String userAccount){

        if (userAccount == null) {
            throw new IllegalArgumentException("입력된 계정정보 없음");
        }

        return usersRepository.existsByUserAccount(userAccount);
    }

    /**
     * 회원 휴대전화 중복 검사
     * @param userPhone 입력된 휴대전화 번호
     * @return 가입 존재 여부 True or False
     */
    @Transactional(readOnly = true)
    public boolean existsByUserPhone(String userPhone){

        if (userPhone == null) {
            throw new IllegalArgumentException("입력된 휴대전화번호 없음");
        }
        return usersRepository.existsByUserPhone(userPhone);
    }

    /**
     * 회원 이메일 중복 검사
     * @param userEmail 입력된 이메일 주소
     * @return 가입 존재 여부 True or False
     */
    @Transactional(readOnly = true)
    public boolean existsByUserEmail(String userEmail){
        if (userEmail == null) {
            throw new IllegalArgumentException("입력된 이메일 정보 없음");
        }
        return usersRepository.existsByUserEmail(userEmail);
    }

    /**
     * 회원가입
     * @param joinForm 입력된 회원 가입 정보 form
     * @return 가입 성공 시 해당 회원번호 반환
     */
    @Transactional
    public Long join(JoinForm joinForm){


        Users users = joinForm.toEntity();
        usersRepository.save(users);
        return users.getId();
    }

//    //유효성검사
//    public Map<String, String> validateHandling(Errors errors){
//        Map<String, String> validatorResult = new HashMap<>();
//
//        for (FieldError error : errors.getFieldErrors()) {
//            String validKeyName = String.format("valid_%s", error.getField());
//            validatorResult.put(validKeyName, error.getDefaultMessage());
//        }
//
//        return validatorResult;
//    }

    /**
     * 로그인
     * @param userAccount 회원 계정
     * @param userPassword 회원 계정 비밀번호
     * @return 회원 정보
     */
    @Transactional
    public Users login(String userAccount, String userPassword){


         Optional<Users> findUser = usersRepository.findByUserAccountAndUserPassword(userAccount, userPassword);

         
         
         return Optional.ofNullable(findUser).orElseThrow(()-> new IllegalArgumentException("조회된 정보 없음")).get();
    }



}
