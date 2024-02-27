package com.example.dw.repository.community;

import com.example.dw.domain.dto.community.QWalkMateDetailReplyDto;
import com.example.dw.domain.dto.community.WalkMateDetailReplyDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.user.QUserFile.userFile;
import static com.example.dw.domain.entity.walkingMate.QWalkingMateComment.walkingMateComment;

@Repository
@RequiredArgsConstructor
public class WalkingMateCommentImpl implements WalkingMateCommentCustom {


    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 산책게시글에 등록된 댓글 목록
     * @param walkBoardId 산책게시글ID
     * @return 해당 산책게시글ID에 등록된 댓글목록
     */
    @Override
    public List<WalkMateDetailReplyDto> findReplyByWalkBoardId(Long walkBoardId) {
        return jpaQueryFactory.select(new QWalkMateDetailReplyDto(
                walkingMateComment.id,
                walkingMateComment.walkingMateCommentContent,
                walkingMateComment.walkingMateCommentRd,
                walkingMateComment.walkingMateCommentMd,
                users.id,
                users.userAccount,
                users.userNickName,
                userFile.id,
                userFile.route,
                userFile.uuid,
                userFile.name

        ))
                .from(walkingMateComment)
                .leftJoin(walkingMateComment.users, users)
                .leftJoin(users.userFile, userFile)
                .where(walkingMateComment.walkingMate.id.eq(walkBoardId))
                .orderBy(walkingMateComment.id.desc())
                .fetch();
    }
}
