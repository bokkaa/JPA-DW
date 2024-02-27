package com.example.dw.controller;

import com.example.dw.domain.dto.community.IndexWalkMateDto;
import com.example.dw.domain.dto.goods.RecentViewGoods;
import com.example.dw.domain.dto.index.WeeklyFreeBoardList;
import com.example.dw.domain.dto.index.WeeklyQnaListDto;
import com.example.dw.service.IndexService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/index/*")
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    //Index

    /**
     * 메인 페이지 - 최근 산책게시글, 주간 인기글(Qna / 자유게시판), 카테고리별 상품, 최근 본 상품
     * @param session session 객체
     * @param model model 객체
     * @return 메인 페이지
     */
    @GetMapping("")
    public String index(HttpSession session, Model model){


        //최근 산책게시글
        List<IndexWalkMateDto> indexWalkMateDtoList = indexService.indexWalkMateList();

        model.addAttribute("indexWalkList", indexWalkMateDtoList);
        System.out.println(indexWalkMateDtoList.toString()+"!@#@!#@!");



        //주간 인기글
        //Qna
        List<WeeklyQnaListDto> weeklyQnaList = indexService.weeklyQnaList();
        model.addAttribute("weeklyQnaBest", weeklyQnaList);
        
        //자유게시판
        List<WeeklyFreeBoardList> weeklyFreeBoardList = indexService.weeklyFreeBoardList();
        model.addAttribute("weeklyFreeBoardBest", weeklyFreeBoardList);
        
        
        
        //최근 본 상품
        List<RecentViewGoods> recentGoods = (List<RecentViewGoods>)session.getAttribute("recentViews");

        model.addAttribute("recentGoods", recentGoods);
        

        return "/index/index";
    }

    //병원찾기
    @GetMapping("/map")
    public String seachMap(){
        return "/map/map";
    }


}
