package com.example.dw.domain.entity.walkingMate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Commit
class WalkingMateTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void EntityTest(){
//        WalkingMate walkingMate = new WalkingMate();
//        WalkingMateAddress walkingMateAddress = new WalkingMateAddress();
//        WalkingMateComment walkingMateComment = new WalkingMateComment();
//
//        walkingMate.setWalkingMateTitle("제목");
//        walkingMate.setWalkingMateContent("내용");
//        walkingMate.setWalkingMateRd(LocalDateTime.now());
//        walkingMate.setWalkingMateMd(LocalDateTime.now());
//        walkingMate.setWalkingMateViewCount(1L);
//        walkingMate.setWalkingMateState(1L);
//        walkingMate.setWalkingMatePerson(2L);
////        walkingMate.setWalkingMateToday(1L);
//        em.persist(walkingMate);
//
//        walkingMateAddress.setWalkingMateAddressCity("서울시");
//        walkingMateAddress.setWalkingMateAddressCountry("노원구");
//        walkingMateAddress.setWalkingMateAddressDetail("우리집");
//        walkingMateAddress.setWalkingMate(walkingMate);
//        em.persist(walkingMateAddress);
//
//        walkingMateComment.setWalkingMateCommentContent("내용");
//        walkingMateComment.setWalkingMateCommentRd(LocalDateTime.now());
//        walkingMateComment.setWalkingMateCommentMd(LocalDateTime.now());
//        walkingMateComment.setWalkingMate(walkingMate);
//        em.persist(walkingMateComment);
    }
}