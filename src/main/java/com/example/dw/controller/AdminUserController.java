package com.example.dw.controller;

import com.example.dw.domain.dto.admin.AdminUserDetailResultDto;
import com.example.dw.domain.entity.user.Users;
import com.example.dw.repository.user.UsersRepository;
import com.example.dw.repository.user.UsersRepositoryCustom;
import com.example.dw.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminUserController {

    private final UsersRepository usersRepository;
    private final UsersRepositoryCustom usersRepositoryCustom;
    private final AdminUserService adminUserService;


    //관리자 페이지 회원현황
    @GetMapping("/userStatus")
    public String userStatus(){
        return "/admin/adminIndex";
    }


    //관리자 페이지 회원 리스트
    @GetMapping("/userList")
    public String userList(){

        return "/admin/adminUserList";
    }

    /**
     *
     * @param userId 회원ID
     * @param model html로 넘기기 위한 model 객체
     * @return 회원 상세 정보
     */
    @GetMapping("/userDetail/{userId}")
    public String userDetail(@PathVariable("userId") Long userId, Model model){

        AdminUserDetailResultDto resultDto = adminUserService.userDetail(userId);

        model.addAttribute("userDetailInfo", resultDto);

        return "/admin/adminUserDetail";
    }

    //관리자 페이지 회원 탈퇴
    @GetMapping("/userDelete/{userId}")
    public RedirectView userDelete(@PathVariable("userId") Long userId){

        Users users = usersRepository.findById(userId).get();
        users.deleteDate();
        usersRepository.deleteById(userId);

        return new RedirectView("/admin/userList");

    }

    //관리자 페이지 회원 복구
    @GetMapping("/userRecover/{userId}")
    public RedirectView userRecover(@PathVariable("userId") Long userId){

        adminUserService.modifyUserStatus(userId);

        return new RedirectView("/admin/userList");
    }


}
