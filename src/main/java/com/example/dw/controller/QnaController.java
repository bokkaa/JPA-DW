package com.example.dw.controller;


import com.example.dw.domain.dto.community.QuestionDetailResultDto;
import com.example.dw.domain.dto.community.QuestionPopularityListDto;
import com.example.dw.domain.form.QuestionWritingForm;
import com.example.dw.repository.community.QuestionRepository;
import com.example.dw.repository.community.QuestionRepositoryCustom;
import com.example.dw.service.FileService;
import com.example.dw.service.QnaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna/*")
public class QnaController {

    private final FileService fileService;
    private final QuestionRepositoryCustom questionRepositoryCustom;
    private final QnaService qnaService;

    @GetMapping("/qnaLists")
    public String page(Model model){
        List<QuestionPopularityListDto> result = questionRepositoryCustom.findAllByQuestion();

        System.out.println(result.toString()+"나와랐!!");

        model.addAttribute("result",result);

//        System.out.println("여기는 controller");
        return "/community/qnaList";
    }

    @GetMapping("/qnawrite")
    public String qnawrite(){



        return "/community/writingQna";
    }


    @PostMapping("/qnaregister")
    public RedirectView write(QuestionWritingForm questionWritingForm,
                              @RequestParam("questionImg")List<MultipartFile> files

    ) throws IOException
    {
        //정보 확인
        System.out.println("[qna 등록 정보] :"+ questionWritingForm.toString());
        files.forEach(r-> System.out.println("[파일목록] : "+r.getOriginalFilename()));

        Long id = qnaService.register(questionWritingForm);
        System.out.println(questionWritingForm.getUserId()+"유저 아이디");

        System.out.println(questionWritingForm.getUserId()+"");

        fileService.registerquestionImg(files, id);
        System.out.println(id);
        System.out.println("여기는 어디게~!");
        return new RedirectView("/qna/qnaLists");
    }

    @GetMapping("/qnaDetail/{questionId}")
    public String detailPage(@PathVariable("questionId") Long questionId , Model model, HttpServletRequest req, HttpServletResponse res){

        // 조회수 업데이트 여부를 결정하는 변수를 선언
        boolean updateCount = true;

        // 쿠키 가져오기(현재 접속 쿠기 배열로 가져오기)
        Cookie[] cookies = req.getCookies();


        if(cookies != null){
            for(Cookie cookie : cookies){
                // "qna_view_count_cookie" 쿠키 확인
                if("qna_view_count_cookie".equals(cookie.getName())){
                    String cookieValue = cookie.getValue();
                    // 벨류 확인...
                    System.out.println(cookieValue + "쿠기 밸류 입니다.");
                    String[] values =  cookieValue.split("_");
                    String qnaId = values[0];

                    long storedTimestamp = Long.parseLong(values[1]);
                    long currentTimestamp = new Date().getTime();

                    if(qnaId.equals(String.valueOf(questionId)) && (currentTimestamp - storedTimestamp) < (24 * 60 * 60 * 1000)){
                        // 해당 게시물에 하룻동안 해당 쿠키가 있었다면 updateCount를 false로 만들어서 더이상 오르지 못하도록 막아준다
                        updateCount =false;
                        break;
                    }

                }
            }
        }


        // 쿠키가 없거나, 하루에 한 번만 업데이트할 경우
        if(updateCount){

            Cookie newCookie = new Cookie("qna_view_count_cookie", questionId + "_" + new Date().getTime());

            System.out.println(newCookie + "새로 만들어진 쿠키 입니다.");

            newCookie.setMaxAge(24 * 60 * 60);

            res.addCookie(newCookie);

            //조회수 증가
            qnaService.updateViewCount(questionId);
            System.out.println("조회수 증가 성공~!!");

        }




        List<QuestionDetailResultDto> detailresult = questionRepositoryCustom.findQnaById(questionId);
        System.out.println(detailresult.toString()+"입니다.");
        model.addAttribute("detail", detailresult);


        return "/community/qnaDetail";
    }


    // 수정중
    @GetMapping("/modify/{questionId}")
    public String modifyPage(@PathVariable("questionId") Long questionId,Model model){
        System.out.println(questionId+ "입니다.");
        List<QuestionDetailResultDto> result = questionRepositoryCustom.findQnaById(questionId);
        System.out.println(result.toString()+"입니다.");
        model.addAttribute("question", result);

        return "/community/writingModifyQna";


    }

    //    // 게시판 수정
    @PutMapping("/modify/{questionId}/edit")
    public RedirectView questionModify(@PathVariable("questionId") Long questionId,
                                       QuestionWritingForm questionWritingForm,
                                       @RequestParam("questionImg") List<MultipartFile> files) throws IOException{

        questionWritingForm.setId(questionId);
        System.out.println("qna 번호 : "+ questionWritingForm.getId());

        qnaService.modify(questionWritingForm,files);
        System.out.println("여기까지 완료!!");
        return new RedirectView("/qna/qnaLists");
    }

    // 게시판 삭제
    @GetMapping("/delete/{questionId}")
    public RedirectView removeQuestion(@PathVariable("questionId") Long questionId){

        qnaService.delete(questionId);

        return new RedirectView("/qna/qnaLists");
    }

}
