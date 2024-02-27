package com.example.dw.service;

import com.example.dw.domain.dto.community.IndexWalkMateDto;
import com.example.dw.domain.dto.goods.IndexGoodsByCateDto;
import com.example.dw.domain.dto.index.WeeklyFreeBoardList;
import com.example.dw.domain.dto.index.WeeklyQnaListDto;
import com.example.dw.domain.entity.goods.GoodsCategory;
import com.example.dw.repository.community.WalkingMateRepositoryCustom;
import com.example.dw.repository.goods.GoodsRepositoryCustom;
import com.example.dw.repository.goods.ShopRepositoryCustom;
import com.example.dw.repository.index.IndexRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexService {

    private final WalkingMateRepositoryCustom walkingMateRepositoryCustom;
    private final GoodsRepositoryCustom goodsRepositoryCustom;
    private final ShopRepositoryCustom shopRepositoryCustom;
    private final IndexRepositoryCustom indexRepositoryCustom;


    /**
     * 산책메이트 최신글
     * @return 
     */
    @Transactional
    public List<IndexWalkMateDto> indexWalkMateList(){

        return walkingMateRepositoryCustom.IndexWalkMateList();

    }


    /**
     * 주간 Qna 인기글 - Top3 (조회수 기준)
     * @return 주간 Qna 인기글
     */
    @Transactional
    public List<WeeklyQnaListDto> weeklyQnaList(){
        return indexRepositoryCustom.weeklyQnaList();
    }

    /**
     * 주간 자유게시판 인기글 - Top3(조회수 기준)
     * @return 주간 자유게시판 인기글
     */
    @Transactional
    public  List<WeeklyFreeBoardList> weeklyFreeBoardList(){

        return indexRepositoryCustom.weeklyFreeBoardList();
    }


    /**
     * 카테고리별 상품
      * @param goodsCategory 상품 카테고리 
     * @return 상품 카테고리별 상품 목록
     */    
    @Transactional
    public List<IndexGoodsByCateDto> indexGoodsByCategory(GoodsCategory goodsCategory){

        return goodsRepositoryCustom.indexGoodsListByCategory(goodsCategory);
    }

}
