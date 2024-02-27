package com.example.dw.repository.community;

import com.example.dw.domain.dto.community.WalkDetailStateDto;
import com.example.dw.domain.entity.walkingMate.WalkingMate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    //산책게시글 신청자 정보
    @Query("select NEW com.example.dw.domain.dto.community.WalkDetailStateDto (wms.walkingMate.id, wms.id, u.id, u.userAccount, u.userNickName, p.id, p.name, p.weight, p.petGender, p.birthDate, p.petCategory, p.neutering, pi.petFileName, pi.petPath, pi.petUuid, wms.state, wms.writerCheck) " +
            "from WalkingMateState wms " +
            "left join WalkingMate wm on wm.id=wms.walkingMate.id " +
            "left join Users u on u.id = wm.users.id " +
            "left join Pet p on p.id = wms.pet.id " +
            "left join PetImg pi on p.id=pi.pet.id " +
            "where wms.walkingMate.id=:walkMateId and wms.state=1 and wms.writerCheck=0")
    List<WalkDetailStateDto> applierPetsInfo(@Param("walkMateId")Long walkMateId);



    //산책게시글 작성제한 (희망 요일 확인)
    @Query("select wm.id from WalkingMate wm where wm.users.id=:userId and wm.walkingMateDate=:walkingMateDate")
    Long limitWrite(@Param("userId")Long userId, @Param("walkingMateDate") String walkingMateDate);

    //산책게시글 수정제한 (이미 작성된 희망요일이 있는지 확인, 없다면 해당하는 희망 요일로 수정 제출 가능)
    @Query("select wm.id from WalkingMate wm where wm.users.id=:userId and wm.walkingMateDate=:walkingMateDate and wm.id=:walkMateId")
    Long limitModify(@Param("userId")Long userId, @Param("walkingMateDate") String walkingMateDate, @Param("walkMateId") Long walkMateId);


}