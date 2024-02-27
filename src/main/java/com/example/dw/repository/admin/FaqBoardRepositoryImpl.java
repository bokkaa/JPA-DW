package com.example.dw.repository.admin;

import com.example.dw.domain.dto.admin.AdminFaqBoardDto;
import com.example.dw.domain.dto.admin.QAdminFaqBoardDto;
import com.example.dw.domain.form.SearchForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.dw.domain.entity.admin.QFaqBoard.faqBoard;

@Repository
public class FaqBoardRepositoryImpl implements FaqBoardRepositoryCustom{


    private final JPAQueryFactory jpaQueryFactory;

    public FaqBoardRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    //관리자 페이지 - FAQ 목록
    @Override
    public Page<AdminFaqBoardDto> findFaqListBySearch(Pageable pageable, SearchForm searchForm) {


        List<AdminFaqBoardDto> content = jpaQueryFactory
                .select(new QAdminFaqBoardDto(
                        faqBoard.id,
                        faqBoard.faqBoardTitle,
                        faqBoard.faqBoardContent,
                        faqBoard.faqBoardViewCount,
                        faqBoard.faqBoardRd,
                        faqBoard.faqBoardMd

                ))
                .from(faqBoard)
                .where(

                        cateKeywordEq(searchForm)
                )
                .orderBy(faqBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long counts = jpaQueryFactory
                .select(faqBoard.count())
                .from(faqBoard)
                .where(
                        cateKeywordEq(searchForm)

                )
                .fetchOne();
        return new PageImpl<>(content, pageable, counts);

    }


    private BooleanExpression cateKeywordEq(SearchForm searchForm){
        if(StringUtils.hasText(searchForm.getCate())&&StringUtils.hasText(searchForm.getKeyword())){
            switch (searchForm.getCate()){
                case "faqBoardTitle" :
                    return faqBoard.faqBoardTitle.containsIgnoreCase(searchForm.getKeyword());
                case "faqBoardContent" :
                    return faqBoard.faqBoardContent.containsIgnoreCase(searchForm.getKeyword());
                default:
                    break;
            }

        }
        return faqBoard.id.isNotNull();
    }

}
