package com.example.dw.service;

import com.example.dw.domain.dto.community.WalkDetailStateDto;
import com.example.dw.domain.dto.community.WalkMateDetailDto;
import com.example.dw.domain.dto.community.WalkMateDetailReplyDto;
import com.example.dw.domain.dto.community.WalkMateListDto;
import com.example.dw.domain.dto.user.UserPetDto;
import com.example.dw.domain.entity.walkingMate.WalkingMate;
import com.example.dw.domain.form.*;
import com.example.dw.repository.community.*;
import com.example.dw.repository.user.UsersRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalkingMateService {


    private final WalkingMateRepository walkingMateRepository;
    private final WalkingMateRepositoryCustom walkingMateRepositoryCustom;
    private final WalkingMateCommentRepository walkingMateCommentRepository;
    private final WalkingMateCommentCustom walkingMateCommentCustom;
    private final UsersRepositoryCustom usersRepositoryCustom;
    private final WalkingMateStateRepository walkingMateStateRepository;


    /**
     * 산책글 리스트
     * @param page page 변수
     * @param searchLocationForm 검색정보가 담겨져 있는 form
     * @return 산책글 리스트
     */
    @Transactional
    public Page<WalkMateListDto> walkMateList(int page , SearchLocationForm searchLocationForm){


        Pageable pageable = PageRequest.of(page, 15);
        return walkingMateRepositoryCustom.findAllWalkMate(pageable, searchLocationForm);
    }


    /**
     * 산책모집글 작성
     * @param walkMateForm 산책모집글 작성 정보
     * @return 산책게시글 ID
     */
    @Transactional
    public Long registerWalkingMate(WalkMateForm walkMateForm){

        //산책글 작성
       WalkingMate walkingMate = walkingMateRepository.save(walkMateForm.toEntity());

       //작성 성공 시 walkingMateState 생성하고 작성자 정보 입력
       registerWalkingMateState(walkingMate.getId(), walkingMate.getUsers().getId(), walkingMate.getPet().getId());



        return walkingMate.getId();
    }


    /**
     * 산책게시글 작성 시 해당 산책글에 대한 신청상태 테이블 자동 생성
     * @param walkingMateId 산책게시글ID
     * @param userId 회원ID
     * @param petId 펫ID
     * @return 산책게시글 상태 엔티티 ID
     */
    @Transactional
    public Long registerWalkingMateState(Long walkingMateId, Long userId, Long petId){

        if (walkingMateId == null) {
            throw new IllegalArgumentException("산책게시글 ID 누락");
        }
        if (userId == null) {
            throw  new IllegalArgumentException("회원ID 누락");
        }

        if (petId == null) {
            throw new IllegalArgumentException("펫ID 누락");
        }

        WalkingMateStateForm walkingMateStateForm = new WalkingMateStateForm();
        walkingMateStateForm.setPetId(petId);
        walkingMateStateForm.setWalkingMateId(walkingMateId);
        walkingMateStateForm.setUserId(userId);
        walkingMateStateForm.setState(1);
        walkingMateStateForm.setWriterCheck(1);

        Long id = walkingMateStateRepository.save(walkingMateStateForm.toEntity()).getId();

        return id;
    }


    /**
     * 산책메이트 신청
     * @param walkingMateStateForm 산책메이트 신청 정보
     */
    @Transactional
    public void applyWalkMate(WalkingMateStateForm walkingMateStateForm){


        walkingMateStateRepository.save(walkingMateStateForm.toEntity());

    }

    /**
     * 산책메이트 신청 중복을 방지하기 위한 중복검사 service
     * @param walkMateId 산책게시글ID
     * @param userId 회원ID
     * @return 새롭게 신청한 경우 => 신청상태 ID 반환 / 이미 신청된 경우 => 0
     */
    @Transactional
    public Long applyCheck(Long walkMateId, Long userId){

        //신청상태
        stateCount(walkMateId);

        try{

            Long id = walkingMateStateRepository.applyCheck(walkMateId, userId);
            return id;

        }catch (Exception e){
            e.printStackTrace();
        }

        return 0L;


    }

    /**
     * 산책메이트 신청 상태 가져오기
     * @param walkMateId 산책게시글ID
     * @return 신청상태( 0 or 1)
     */
    @Transactional
    public Long stateCount(Long walkMateId){

        if (walkMateId == null) {
            throw new IllegalArgumentException("산책게시글ID 누락");
        }

        Long count = walkingMateStateRepository.stateCount(walkMateId);

        System.out.println("모집상태 1 개수 : " + count);

        return count;
    }

    /**
     * 산책메이트 신청 철회
     * @param walkMateId 산책게시글ID
     * @param userId 회원ID
     */
    @Transactional
    public void applyCanCel(Long walkMateId, Long userId){

        if (walkMateId == null) {
            throw new IllegalArgumentException("산책게시글ID 누락");
        }
        if (userId == null) {
            throw new IllegalArgumentException("회원ID 누락");
        }


        Long id = walkingMateStateRepository.applyCheck(walkMateId, userId);

        walkingMateRepository.downDateWalkMateState(walkMateId);
        walkingMateStateRepository.deleteById(id);

    }


    /**
     * 산책글 상태값 확인
     * @param userId 회원ID
     * @param walkMateId 산책게시글ID
     * @return 신청회원 ID
     */
    @Transactional
    public Long applyState(Long userId, Long walkMateId){

        if (userId == null) {
            throw new IllegalArgumentException("회원ID 누락");
        }
        if (walkMateId == null) {
            throw new IllegalArgumentException("산책게시글ID 누락");
        }

        Long applyUserId = walkingMateStateRepository.applyState(userId, walkMateId);

        return applyUserId;

    }

    /**
     * 산책글 신청자 펫 정보
     * @param walkMateId 산책게시글ID
     * @return 산책글 신청자 펫 정보
     */
    @Transactional
    public List<WalkDetailStateDto> findApplierPetInfo(Long walkMateId){

        if (walkMateId == null) {
            throw new IllegalArgumentException("산책게시글Id 누락");
        }

        List<WalkDetailStateDto> petInfos = walkingMateRepository.applierPetsInfo(walkMateId);

        System.out.println(petInfos.toString());

        return petInfos;

    }


    /**
     * 해당 회원이 마이페이지에서 등록한 펫 목록
     * @param userId 회원ID
     * @return 해당 회원이 마이페이지에서 등록한 펫 목록
     */
    @Transactional
    public List<UserPetDto> getUserPets(Long userId){

        if (userId == null) {
            throw new IllegalArgumentException("회원ID 누락");
        }

       return usersRepositoryCustom.findAllPetByUserId(userId);
    }


    /**
     * 산책글 상세보기
     * @param walkBoardId 산책게시글ID
     * @return 해당 산책게시글ID에 맞는 산책글 상세 정보
     */
    @Transactional
    public Optional<WalkMateDetailDto> walkDetailPage(Long walkBoardId){


        if (walkBoardId == null) {
            throw new IllegalArgumentException("산책게시글Id 누락");
        }


        //조회수 증가
        walkingMateRepository.updateViewCount(walkBoardId);

        return walkingMateRepositoryCustom.walkMateDetail(walkBoardId);
    }


    /**
     * 산책게시글 수정
     * @param walkMateForm 산책게시글 수정 정보가 담긴 form
     */
    @Transactional
    public void walkModify(WalkMateForm walkMateForm){

        WalkingMate walkingMate = walkingMateRepository.findById(walkMateForm.getId()).get();

        if(walkMateForm.getWalkingMateDate().equals(walkingMate.getWalkingMateDate())){
            walkingMate.updateExceptDate(walkMateForm);
        }else {
            walkingMate.update(walkMateForm);
        }

    }

    /**
     * 산책게시글 삭제
     * @param walkBoardId 산책게시글ID
     */
    @Transactional
    public void walkDelete(Long walkBoardId){

        if (walkBoardId == null) {
            throw new IllegalArgumentException("산책게시글Id 누락");
        }

        walkingMateRepository.deleteById(walkBoardId);

    }


    /**
     * 산책게시글 작성제한 - 희망 요일을 기준으로 그 요일에 해당하는 날짜에 글작성 1회만 가능
     * @param userId
     * @param walkMateDate
     * @return 작성가능 => 0, 작성불가능 => 1
     */
    //작성제한
    @Transactional
    public Integer limitWriteByDay(Long userId, String walkMateDate){

        if (userId == null) {
            throw new IllegalArgumentException("회원ID 누락");
        }

        if (walkMateDate == null) {
            throw new IllegalArgumentException("희망 신청일자 누락");
        }

        try{

            Long id  =walkingMateRepository.limitWrite(userId, walkMateDate);


            if(id != null){
                return 1;
            }else {
                return 0;
            }


        }catch (NullPointerException e){
            e.printStackTrace();
            return 0;
        }

    }



    
    //산책게시글 댓글



    /**
     * 산책게시글 댓글 리스트
     * @param walkBoardId 산책게시글ID
     * @return 해당 산책게시글에 등록되어있는 댓글 리스트
     */
    //산책글 댓글 목록
    @Transactional
    public List<WalkMateDetailReplyDto> getReplyList(Long walkBoardId){

        if (walkBoardId == null) {
            throw new IllegalArgumentException("산책게시글ID 누락");
        }

        return walkingMateCommentCustom.findReplyByWalkBoardId(walkBoardId);

    }

    /**
     * 산책게시글 댓글 등록
     * @param walkingMateCommentForm 산책게시글 댓글ID
     */
    @Transactional
    public void walkDetailReply(WalkingMateCommentForm walkingMateCommentForm){

        walkingMateCommentRepository.save(walkingMateCommentForm. toEntity());
    }

    /**
     * 산책게시글 댓글 수정
     * @param walkingMateCommentForm 댓글 수정 정보가 담긴 form
     */
    @Transactional
    public void modifyReply(WalkingMateCommentForm walkingMateCommentForm){

        walkingMateCommentRepository.updateComment(walkingMateCommentForm.getWalkBoardComment(), walkingMateCommentForm.getId());

    }

    /**
     * 산책게시글 댓글 삭제
     * @param walkCommentId 산책게시글 댓글ID
     */
    @Transactional
    public void deleteReply(Long walkCommentId){

        if (walkCommentId == null) {
            throw new IllegalArgumentException("산책게시글 댓글ID 누락");
        }

        walkingMateCommentRepository.deleteById(walkCommentId);
    }
}
