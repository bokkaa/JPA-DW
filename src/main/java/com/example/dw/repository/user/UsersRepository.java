package com.example.dw.repository.user;

import com.example.dw.domain.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    //아이디 중복체크
    boolean existsByUserAccount(String usersAccount);
    //휴대폰번호 중복체크
    boolean existsByUserPhone(String userPhone);
    //이메일 중복체크
    boolean existsByUserEmail(String userEmail);


    //닉네임 중복 체크
    boolean existsByUserNickName(String userNickName);



    //계정 정보 찾기
    boolean existsByUserNameAndUserEmailAndUserPhone(String userName, String userEmail, String userPhone);


    //아이디찾기
    Optional<Users> findUserAccountByUserNameAndUserEmailAndUserPhone(String userName, String userEmail, String userPhone);

    //비밀번호 찾기
    Optional<Users> findByUserNameAndUserAccountAndUserEmail(String userName, String userAccount, String userEmail);





    //비밀번호 찾기
    boolean existsByUserNameAndUserAccountAndUserEmail(String userName, String userAccount, String userEmail);

    //로그인
    Optional<Users> findByUserAccountAndUserPassword(String userAccount, String userPassword);

    // 유저 정보 찾아오기
    Optional<Users> findById(Long id);

    Optional<Users> findByUserFileId(Long userFileId);

    //유저 삭제
    void deleteUsersById(Long userid);

}
