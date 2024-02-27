package com.example.dw.repository.index;

import com.example.dw.domain.dto.index.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.example.dw.domain.entity.freeBoard.QFreeBoard.freeBoard;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class IndexRepositoryImpl implements IndexRepositoryCustom{


    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    private static final int MAX_QUESTION_RESULTS = 3;
    private static final int MAX_FREE_RESULTS = 5;

    //해당 날짜가 들어있는 주간날짜 구하기
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime thisWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
    LocalDateTime thisWeekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toLocalDate().atTime(23, 59, 59);

    /**
     * 주간 인기 Qna - Top3 조회수 기준
     * @return
     */
    @Override
    public List<WeeklyQnaListDto> weeklyQnaList() {


        List<WeeklyQnaList> query = em.createQuery(
                "SELECT NEW com.example.dw.domain.dto.index.WeeklyQnaList(" +
                        "q.id, q.users.id, q.users.userAccount, q.users.userNickName, q.questionTitle, q.questionViewCount, " +
                        "qi.questionImgRoute, qi.questionImgUuid, qi.questionImgName) " +
                        "FROM Question q " +
                        "LEFT JOIN q.questionImg qi " +
                        "WHERE qi.id = (SELECT MIN(qi2.id) FROM QuestionImg qi2 WHERE q.id = qi2.question.id) and q.questionRd between :thisWeekStart And :thisWeekEnd " +
                        "ORDER BY q.questionViewCount desc ", WeeklyQnaList.class)
                .setParameter("thisWeekStart", thisWeekStart)
                .setParameter("thisWeekEnd", thisWeekEnd)
                .getResultList();


        return query.stream().map(
                o -> new WeeklyQnaListDto(
                        o.getQnaBoardId(),
                        o.getWriterUserId(),
                        o.getWriterUserAccount(),
                        o.getWriterUserNickName(),
                        o.getQnaBoardTitle(),
                        o.getQuestionViewCount(),
                        new WeeklyQnaListImg(
                                o.getQuestionImgRoute(),
                                o.getQuestionImgUuid(),
                                o.getQuestionImgName()
                        )
                )
        ).limit(MAX_QUESTION_RESULTS).collect(toList());


    }

    /**
     * 주간 자유게시판 인기글 - Top3 조회수 기준
     * @return
     */
    @Override
    public List<WeeklyFreeBoardList> weeklyFreeBoardList() {


        List<WeeklyFreeBoardList> weeklyFreeLists = jpaQueryFactory.select(
                new QWeeklyFreeBoardList(
                        freeBoard.id,
                        freeBoard.freeBoardTitle,
                        freeBoard.freeBoardViewCount
                )
        )
                .from(freeBoard)
                .where(freeBoard.freeBoardRd.between(thisWeekStart, thisWeekEnd))
                .orderBy(freeBoard.freeBoardViewCount.desc())
                .limit(MAX_FREE_RESULTS)
                .fetch();
        return weeklyFreeLists;
    }


}
