package com.example.dw.repository.admin;


import com.example.dw.domain.dto.admin.AdminNoticeBoardDto;
import com.example.dw.domain.dto.admin.QAdminNoticeBoardDto;
import com.example.dw.domain.dto.notice.NoticeDetailDto;
import com.example.dw.domain.dto.notice.NoticeListDto;
import com.example.dw.domain.dto.notice.QNoticeDetailDto;
import com.example.dw.domain.dto.notice.QNoticeListDto;
import com.example.dw.domain.form.SearchForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.dw.domain.entity.admin.QNoticeBoard.noticeBoard;

@Repository
@RequiredArgsConstructor
public class NoticeBoardRepositoryImpl implements NoticeBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AdminNoticeBoardDto> findNoticeListBySearch(Pageable pageable, SearchForm searchForm) {


        List<AdminNoticeBoardDto> content = jpaQueryFactory
                .select(new QAdminNoticeBoardDto(
                        noticeBoard.id,
                        noticeBoard.noticeBoardTitle,
                        noticeBoard.noticeBoardContent,
                        noticeBoard.noticeBoardViewCount,
                        noticeBoard.noticeBoardRd,
                        noticeBoard.noticeBoardMd
                ))
                .from(noticeBoard)
                .where(
                        cateKeywordEq(searchForm)
                )
                .orderBy(noticeBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory
                .select(noticeBoard.count())
                .from(noticeBoard)
                .where(
                        cateKeywordEq(searchForm)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }




    private BooleanExpression cateKeywordEq(SearchForm searchForm){
        if(StringUtils.hasText(searchForm.getCate())&&StringUtils.hasText(searchForm.getKeyword())){

            switch (searchForm.getCate()){

                case "noticeBoardTitle" :
                    return noticeBoard.noticeBoardTitle.containsIgnoreCase(searchForm.getKeyword());
                case "noticeBoardContent" :
                    return noticeBoard.noticeBoardContent.containsIgnoreCase(searchForm.getKeyword());
                default:
                    break;
            }

        }
        return noticeBoard.id.isNotNull();
    }

    /**
     * 여기서 부터 Notice Board
     */
    private BooleanExpression noticeBoardTitleEq(String keyword){
        return StringUtils.hasText(keyword) ? noticeBoard.noticeBoardTitle.containsIgnoreCase(keyword) : null;
    }

    private Long getCount(String keyword) {
        Long count = jpaQueryFactory
                .select(noticeBoard.count())
                .from(noticeBoard)
                .where(noticeBoardTitleEq(keyword))
                .fetchOne();
        return count;
    }

    @Override
    public Page<NoticeListDto> findNoticeBoardListBySearch(Pageable pageable, String keyword) {

        //검색
        BooleanExpression keywordTitle = noticeBoardTitleEq(keyword);

        List<NoticeListDto> contents = jpaQueryFactory
                .select(new QNoticeListDto(
                        noticeBoard.id,
                        noticeBoard.noticeBoardTitle,
                        noticeBoard.noticeBoardContent,
                        noticeBoard.noticeBoardViewCount,
                        noticeBoard.noticeBoardRd,
                        noticeBoard.noticeBoardMd
                ))
                .from(noticeBoard)
                .where(keywordTitle)
                .orderBy(noticeBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //페이징을 위한 전체 데이터 수 조회
        Long count = getCount(keyword);

        System.out.println(contents.toString()+"리스트");

        return new PageImpl<>(contents, pageable,count);
    }

    @Override
    public List<NoticeDetailDto> findNoticeById(Long id) {
        System.out.println(id + "공지사항 아이디 조회");
        List<NoticeDetailDto> noticeDetailDtos =
         jpaQueryFactory
                .select(new QNoticeDetailDto(
                noticeBoard.id,
                noticeBoard.noticeBoardTitle,
                noticeBoard.noticeBoardContent,
                noticeBoard.noticeBoardViewCount,
                noticeBoard.noticeBoardRd,
                noticeBoard.noticeBoardMd
        ))
                .from(noticeBoard)
                .where(noticeBoard.id.eq(id))
                .fetch();

        noticeDetailDtos.forEach(r -> System.out.println(r.toString()+"noticeDetailDtos 조회"));

        return noticeDetailDtos;
    }

    //공지사항 자주찾는글 top3
    @Override
    public List<NoticeListDto> findNoticeRankListById() {
        return jpaQueryFactory.select(new QNoticeListDto(
                noticeBoard.id,
                noticeBoard.noticeBoardTitle,
                noticeBoard.noticeBoardContent,
                noticeBoard.noticeBoardViewCount,
                noticeBoard.noticeBoardRd,
                noticeBoard.noticeBoardMd
        ))
                .from(noticeBoard)
                .orderBy(noticeBoard.noticeBoardViewCount.desc())
                .limit(3)
                .fetch();
    }
}

