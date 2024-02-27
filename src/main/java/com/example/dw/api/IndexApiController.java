package com.example.dw.api;

import com.example.dw.domain.dto.goods.IndexGoodsByCateDto;
import com.example.dw.domain.dto.index.WeeklyQnaListDto;
import com.example.dw.domain.entity.goods.GoodsCategory;
import com.example.dw.service.IndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/indexes/*")
@RequiredArgsConstructor
public class IndexApiController {

    @Value("${file.dir}")
    private String goodsImg;

    private final IndexService indexService;



    //시험용
    @GetMapping("/weeklyQnaBest")
    public List<WeeklyQnaListDto> weeklyQnaLists(){
        return indexService.weeklyQnaList();
    }


    /**
     * 상품 카테고리별 상품 목록
     * @param goodsCategory 상품 카테고리
     * @return
     */
    @GetMapping("/goodsByCate")
    public List<IndexGoodsByCateDto> goodsByCategory(GoodsCategory goodsCategory){


        System.out.println(goodsCategory+"!@#!@#!@#");
        return indexService.indexGoodsByCategory(goodsCategory);

    }

    /**
     * 상품 카테고리별 상품 사진
     * @param fileFullPath 상품 사진 경로
     * @return
     * @throws IOException
     */
    @GetMapping("/goodsImg")
    public byte[] getEmpImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(goodsImg, fileFullPath));
    }



}
