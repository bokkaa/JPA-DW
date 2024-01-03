package com.example.dw.repository.community;

import com.example.dw.domain.entity.walkingMate.WalkingMate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalkingMateRepository extends JpaRepository<WalkingMate, Long> {


    //조회수 카운트
    @Modifying
    @Query("update WalkingMate wm set wm.walkingMateViewCount = wm.walkingMateViewCount+1 where wm.id=:id")
    void updateViewCount(@Param("id") Long id);

    //walkMate-state 상태값 변경
    @Modifying
    @Query("update WalkingMate wm set wm.walkingMateState=1 where wm.id=:id")
    void updateWalkMateState(@Param("id") Long id);

    //walkMate-state 상태값 변경
    @Modifying
    @Query("update WalkingMate wm set wm.walkingMateState=0 where wm.id=:id")
    void downDateWalkMateState(@Param("id") Long id);

}