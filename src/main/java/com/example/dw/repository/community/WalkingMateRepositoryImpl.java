package com.example.dw.repository.community;


import com.example.dw.domain.dto.admin.AdminWalkMateDetailDto;
import com.example.dw.domain.dto.admin.ApplierUserList;
import com.example.dw.domain.dto.admin.QAdminWalkMateDetailDto;
import com.example.dw.domain.dto.admin.QApplierUserList;
import com.example.dw.domain.dto.community.*;
import com.example.dw.domain.form.SearchCateLocationForm;
import com.example.dw.domain.form.SearchForm;
import com.example.dw.domain.form.SearchLocationForm;
import com.example.dw.domain.form.SearchRecruitmentForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.example.dw.domain.entity.user.QPet.pet;
import static com.example.dw.domain.entity.user.QPetImg.petImg;
import static com.example.dw.domain.entity.user.QUserFile.userFile;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.walkingMate.QWalkingMate.walkingMate;
import static com.example.dw.domain.entity.walkingMate.QWalkingMateComment.walkingMateComment;
import static com.example.dw.domain.entity.walkingMate.QWalkingMateState.walkingMateState;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class WalkingMateRepositoryImpl implements WalkingMateRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;

    private static final Integer Max_Result = 7;

    //산책글 모집현황

    /**
     * 산책메이트 게시물 목록
     * @param pageable Pageable 페이징
     * @param searchLocationForm 검색 정보
     * @return 산책메이트 게시물 목록
     */
    @Override
    public Page<WalkMateListDto> findAllWalkMate(Pageable pageable, SearchLocationForm searchLocationForm) {

        List<WalkMateListDto> contents = jpaQueryFactory.select(new QWalkMateListDto(
                walkingMate.id,
                walkingMate.walkingMateTitle,
                walkingMate.walkingMateRd,
                walkingMate.walkingMateViewCount,
                walkingMate.walkingMateState,
                walkingMate.walkingMatePerson,
                walkingMateState.count(),
                walkingMate.walkingMateDate,
                walkingMate.walkingMateTime,
                walkingMate.walkCity,
                walkingMate.walkCounty,
                users.id,
                users.userNickName,
                users.userAccount
        ))
                .from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(walkingMate.walkingMateStateList, walkingMateState)
                .where(
                        areaNameEq(searchLocationForm),
                        createRecruitmentStatusCondition(searchLocationForm),
                        cityNameEq(searchLocationForm),
                        countyNameEq(searchLocationForm)
                )
                .where(walkingMateState.state.eq(1))
                .groupBy(
                        walkingMate.id,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateViewCount,
                        walkingMate.walkingMateState,
                        walkingMate.walkingMatePerson,
                        walkingMate.walkingMateDate,
                        walkingMate.walkingMateTime,
                        walkingMate.walkCity,
                        walkingMate.walkCounty,
                        users.id,
                        users.userNickName,
                        users.userAccount
                )
                .orderBy(walkingMate.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long counts =  jpaQueryFactory.select(
                walkingMate.count()
                )
                .from(walkingMate)
                .where(
                        areaNameEq(searchLocationForm),
                        createRecruitmentStatusCondition(searchLocationForm),
                        cityNameEq(searchLocationForm),
                        countyNameEq(searchLocationForm)
                )
                .fetchOne();



        return new PageImpl<>(contents, pageable, counts);

    }


    /**
     * 산책메이트 게시물 상세 정보 조회
     * @param walkBoardId 산책메이트 게시판ID
     * @return 산책메이트 게시물 상세 정보
     */
    @Override
    public Optional<WalkMateDetailDto> walkMateDetail(Long walkBoardId) {
        return Optional.ofNullable(jpaQueryFactory.select(new QWalkMateDetailDto(
                walkingMate.id,
                walkingMate.users.id,
                users.userNickName,
                users.userAccount,
                walkingMate.walkingMateRd,
                walkingMate.walkingMateMd,
                walkingMate.walkingMateTitle,
                walkingMate.walkingMateContent,
                walkingMateState.count(),
                walkingMate.walkingMatePerson,
                walkingMate.walkingMateDate,
                walkingMate.walkingMateTime,
                walkingMate.walkingMateFullAddress,
                walkingMate.walkCity,
                walkingMate.walkCounty,
                pet.id,
                pet.name,
                pet.petCategory,
                pet.weight,
                pet.neutering,
                pet.petGender,
                petImg.id,
                petImg.petPath,
                petImg.petUuid,
                petImg.petFileName,
                walkingMate.walkingMateViewCount,
                walkingMate.walkingMateState


        ))
                .from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(walkingMate.pet, pet)
                .leftJoin(pet.petImg, petImg)
                .leftJoin(walkingMate.walkingMateStateList, walkingMateState)
                .where(walkingMate.id.eq(walkBoardId).and(walkingMateState.state.eq(1)))
                .groupBy( walkingMate.id,
                        walkingMate.users.id,
                        users.userNickName,
                        users.userAccount,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateMd,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateContent,
                        walkingMate.walkingMatePerson,
                        walkingMate.walkingMateDate,
                        walkingMate.walkingMateTime,
                        walkingMate.walkingMateFullAddress,
                        walkingMate.walkCity,
                        walkingMate.walkCounty,
                        pet.id,
                        pet.name,
                        pet.petCategory,
                        pet.weight,
                        pet.neutering,
                        pet.petGender,
                        petImg.id,
                        petImg.petPath,
                        petImg.petUuid,
                        petImg.petFileName,
                        walkingMate.walkingMateViewCount,
                        walkingMate.walkingMateState
                )
                .fetchOne());
    }


    /**
     * 관리자 페이지 - 산책메이트 게시글 목록
     * @param pageable Pageable 페이징
     * @param searchCateLocationForm 갬색정보
     * @return 산책메이트 게시글 목록
     */
    @Override
    public Page<WalkMateListDto> findAllWalkMate(Pageable pageable, SearchCateLocationForm searchCateLocationForm) {

        SearchLocationForm searchLocationForm = new SearchLocationForm(
                searchCateLocationForm.getArea(),
                searchCateLocationForm.getCity(),
                searchCateLocationForm.getCounty(),
                searchCateLocationForm.getState()
        );

        List<WalkMateListDto> content = jpaQueryFactory.select(new QWalkMateListDto(
                walkingMate.id,
                walkingMate.walkingMateTitle,
                walkingMate.walkingMateRd,
                walkingMate.walkingMateViewCount,
                walkingMate.walkingMateState,
                walkingMateState.count(),
                walkingMate.walkingMatePerson,
                walkingMate.walkingMateDate,
                walkingMate.walkingMateTime,
                walkingMate.walkCity,
                walkingMate.walkCounty,
                users.id,
                users.userNickName,
                users.userAccount

        ))      .from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(walkingMate.walkingMateStateList, walkingMateState)
                .where(
                        cateKeywordEq(new SearchForm(searchCateLocationForm.getCate(), searchCateLocationForm.getKeyword())),
                        createRecruitmentStatusCondition(searchLocationForm),
                        areaNameEq(searchLocationForm),
                        cityNameEq(searchLocationForm),
                        countyNameEq(searchLocationForm),
                        dateEq(searchCateLocationForm.getStartDate(), searchCateLocationForm.getEndDate())
                )
                .where(walkingMateState.state.eq(1)) //모집 상태
                .groupBy(
                        walkingMate.id,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateViewCount,
                        walkingMate.walkingMateState,
                        walkingMate.walkingMatePerson,
                        walkingMate.walkingMateDate,
                        walkingMate.walkingMateTime,
                        walkingMate.walkCity,
                        walkingMate.walkCounty,
                        users.id,
                        users.userNickName,
                        users.userAccount

                )
                .orderBy(walkingMate.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory.select(walkingMate.count()
        )
                .from(walkingMate)
                .where(
                        cateKeywordEq(new SearchForm(searchCateLocationForm.getCate(), searchCateLocationForm.getKeyword())),
                        createRecruitmentStatusCondition(searchLocationForm),
                        areaNameEq(searchLocationForm),
                        cityNameEq(searchLocationForm),
                        countyNameEq(searchLocationForm),
                        dateEq(searchCateLocationForm.getStartDate(), searchCateLocationForm.getEndDate())


                )
                .fetchOne();
        return new PageImpl<>(content, pageable, count);

    }


    /**
     * 관리자 페이지 - 산책메이트 게시물 상세 정보
     * @param walkMateId 산책메이트 게시물ID
     * @return 산책메이트 게시물 상세 정보
     */
    @Override
    public AdminWalkMateDetailDto adminWalkMateDetail(Long walkMateId) {

        //산책메이트 게시글 정보
        AdminWalkMateDetailDto adminWalkMateDetailDto = jpaQueryFactory.select(
                new QAdminWalkMateDetailDto(
                        walkingMate.id,
                        walkingMate.users.id,
                        walkingMate.users.userAccount,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateMd,
                        walkingMate.walkingMateViewCount,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateContent,
                        walkingMate.walkingMateFullAddress,
                        walkingMate.walkingMateDate,
                        walkingMate.walkingMateTime,
                        pet.id,
                        pet.name,
                        walkingMate.walkingMateState
                )).from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(users.pet, pet)
                 .where(walkingMate.id.eq(walkMateId))
                 .fetchOne();

        //산책메이트 게시글 신청자 정보
        List<ApplierUserList> applierUserListList = jpaQueryFactory.select(new QApplierUserList(
                                walkingMateState.id,
                               walkingMateState.users.id,
                                walkingMateState.users.userAccount,
                                pet.id,
                                pet.petCategory,
                                pet.name,
                                pet.weight,
                                pet.petGender,
                                pet.neutering,
                                pet.birthDate,
                                petImg.petPath,
                                petImg.petUuid,
                                petImg.petFileName,
                                walkingMateState.state
                ))
                .from(walkingMateState)
                .leftJoin(walkingMateState.users, users)
                .leftJoin(walkingMateState.pet, pet)
                .leftJoin(pet.petImg, petImg)
                .where(walkingMate.id.eq(walkMateId).and(walkingMateState.writerCheck.eq(0)))
                .fetch();

        //산책메이트 게시글 댓글 정보
        List<WalkMateDetailReplyDto> replyDtos =
                       jpaQueryFactory.select(new QWalkMateDetailReplyDto(
                                walkingMateComment.id,
                                walkingMateComment.walkingMateCommentContent,
                                walkingMateComment.walkingMateCommentRd,
                                walkingMateComment.walkingMateCommentMd,
                                walkingMateComment.users.id,
                                walkingMateComment.users.userAccount,
                                walkingMateComment.users.userNickName,
                                userFile.id,
                                userFile.route,
                                userFile.uuid,
                                userFile.name
                        ))
                               .from(walkingMateComment)
                               .leftJoin(walkingMateComment.walkingMate, walkingMate)
                               .leftJoin(walkingMateComment.users, users)
                               .leftJoin(users.userFile, userFile)
                               .where(walkingMateComment.walkingMate.id.eq(walkMateId))
                               .fetch();



        adminWalkMateDetailDto.setApplierUserListList(applierUserListList);
        adminWalkMateDetailDto.setWalkMateDetailReplyDtos(replyDtos);

        return adminWalkMateDetailDto;
    }

    /**
     * 메인 페이지 - 산책메이트 게시글 목록
     * @return 산책메이트 게시글 목록
     */
    @Override
    public List<IndexWalkMateDto> IndexWalkMateList() {
        return jpaQueryFactory.select(new QIndexWalkMateDto(
                walkingMate.id,
                walkingMate.walkCity,
                walkingMate.walkCounty,
                walkingMate.walkingMateTitle,
                walkingMate.walkingMateState,
                users.id,
                petImg.petPath,
                petImg.petUuid,
                petImg.petFileName
        ))
                .from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(walkingMate.pet, pet)
                .leftJoin(pet.petImg, petImg)
                .where(walkingMate.walkingMateState.eq(0L))
                .orderBy(walkingMate.id.desc())
                .limit(Max_Result)
                .fetch();
    }


    /**
     * 산책메이트 게시글 목록 검색
     * @param searchLocationForm 모집 상태
     * @return
     */
    private BooleanExpression createRecruitmentStatusCondition(SearchLocationForm searchLocationForm) {
        try{
            if (searchLocationForm.getState().equals("0")) { // 모집중
                return walkingMate.walkingMateState.eq(0L);
            } else if (searchLocationForm.getState().equals("1")) { // 모집완료
                return walkingMate.walkingMateState.eq(1L);
            } else { // 전체보기 ('' 인 경우)
                return null; // 특정 조건 없이 모든 결과 반환
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 산책메이트 게시글 목록 검색
     * @param searchLocationForm 권역
     * @return
     */
    private BooleanExpression areaNameEq(SearchLocationForm searchLocationForm){

        if(searchLocationForm.getArea().equals("수도권")){
            return walkingMate.walkCity.in("서울", "경기", "인천");
        } else if(searchLocationForm.getArea().equals("강원권")){
            return walkingMate.walkCity.eq("강원");
        } else if(searchLocationForm.getArea().equals("충청권")){
            return walkingMate.walkCity.in("충북", "충남", "대전", "세종");
        }   else if(searchLocationForm.getArea().equals("전라권")){
            return walkingMate.walkCity.in("전북", "전남", "광주");
        }   else if(searchLocationForm.getArea().equals("경상권")){
            return walkingMate.walkCity.in("경북", "경남", "부산", "대구", "울산");
        } else if(searchLocationForm.getArea().equals("제주권")){
            return walkingMate.walkCity.eq("제주");
        } else {
            return null;

        }
    }

    /**
     * 산책메이트 게시글 목록 검색
     * @param searchLocationForm 도시
     * @return
     */
    private BooleanExpression cityNameEq(SearchLocationForm searchLocationForm){
        return StringUtils.hasText(searchLocationForm.getCity()) ? walkingMate.walkCity.eq(searchLocationForm.getCity()) : null;
    }

    /**
     * 산책메이트 게시글 목록 검색
     * @param searchLocationForm 행정구, 군 등
     * @return
     */
    private BooleanExpression countyNameEq(SearchLocationForm searchLocationForm){
        return StringUtils.hasText(searchLocationForm.getCounty()) ? walkingMate.walkCounty.eq(searchLocationForm.getCounty()) : null;
    }


    /**
     * 관리자 페이지 - 산책메이트 게시글 목록 검색
     * @param searchForm 검색 카테고리 - 제목/작성자
     * @return
     */
    private BooleanExpression cateKeywordEq(SearchForm searchForm){
        if(StringUtils.hasText(searchForm.getCate())&&StringUtils.hasText(searchForm.getKeyword())){

            switch (searchForm.getCate()){

                case "walkListTitle" :
                    return walkingMate.walkingMateTitle.containsIgnoreCase(searchForm.getKeyword());
                case "walkListWriter" :
                    return walkingMate.users.userAccount.containsIgnoreCase(searchForm.getKeyword());
                default:
                    break;
            }

        }
        return walkingMate.id.isNotNull();
    }

    /**
     * 관리자 페이지 - 산책메이트 게시글 목록 검색
     * @param startDate 시작날짜
     * @param endDate 종료날짜
     * @return
     */
    private BooleanExpression dateEq(String startDate, String endDate) {

        //시작날짜와 종료날짜가 모두 없을 때
        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            if (!startDate.isEmpty()) {
                LocalDate localStartDate = LocalDate.parse(startDate, formatter);
                start = localStartDate.atStartOfDay();
            }

            if (!endDate.isEmpty()) {
                LocalDate localEndDate = LocalDate.parse(endDate, formatter);
                end = localEndDate.atTime(LocalTime.MAX);
            }

            if (start != null && end != null && end.isBefore(start)) {
                LocalDateTime temp = end;
                end = start;
                start = temp;
                System.out.println("서로 바껴서 선택했을 때");
                System.out.println("시작날짜" + start);
                System.out.println("종료날짜" + end);
            }

            if (start != null && end != null) {
                System.out.println("정상적인 검색");
                return Expressions.allOf(walkingMate.walkingMateRd.between(start, end));
            } else if (start != null) {
                System.out.println("종료날짜가 없어");
                return Expressions.allOf(walkingMate.walkingMateRd.after(start));
            } else if (end != null) {
                System.out.println("시작날짜가 없어");
                return Expressions.allOf(walkingMate.walkingMateRd.before(end));
            }
        } catch (DateTimeException e) {
            e.printStackTrace();
        }

        return null;
    }


    //마이페이지 내가작성한 메이트글 조회


    @Override
    public Page<WalkMateMyDetailListDto> findAllWalkMateAndUserId(Pageable pageable, SearchRecruitmentForm searchRecruitmentForm, Long userId) {
        System.out.println(searchRecruitmentForm.getState());


        List<WalkMateMyListDto> contents = jpaQueryFactory.select(new QWalkMateMyListDto(
                walkingMate.id,
                walkingMate.walkingMateTitle,
                walkingMate.walkingMateContent,
                walkingMate.walkingMateRd,
                walkingMate.walkingMateViewCount,
                walkingMate.walkingMateState,
                walkingMate.walkingMatePerson,
                walkingMateState.count(),
                walkingMate.walkingMateDate,
                walkingMate.walkingMateTime,
                walkingMate.walkCity,
                walkingMate.walkCounty,
                users.id,
                users.userNickName,
                users.userAccount
        ))
                .from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(walkingMate.walkingMateStateList,walkingMateState)
                .where(
                        createStatusCondition(searchRecruitmentForm)

                )
                .where(walkingMateState.state.eq(1))
                .groupBy(
                        walkingMate.id,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateContent,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateViewCount,
                        walkingMate.walkingMateState,
                        walkingMate.walkingMatePerson,
                        walkingMate.walkingMateDate,
                        walkingMate.walkingMateTime,
                        walkingMate.walkCity,
                        walkingMate.walkCounty,
                        users.id,
                        users.userNickName,
                        users.userAccount

                )
                .having(
                        walkingMate.users.id.eq(userId)
                )
                .orderBy(walkingMate.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long counts = jpaQueryFactory.select(
                walkingMate.count()
        )
                .from(walkingMate)
                .where(
                        createStatusCondition(searchRecruitmentForm),
                        walkingMate.users.id.eq(userId)

                )
                .fetchOne();

        System.out.println(contents + "항목");
        System.out.println(counts + "수량");


        List<WalkMateMyDetailListDto> result = contents.stream().map(walkmate -> {
            List<WalkMateStateDto> walkMateStateDto = jpaQueryFactory.select(new QWalkMateStateDto
                    (
                            walkingMateState.id,
                            walkingMate.id,
                            users.id,
                            users.userAccount,
                            users.userNickName,
                            walkingMateState.state,
                            walkingMateState.writerCheck,
                            users.address.address,
                            users.address.detail
                    ))
                    .from(walkingMateState)
                    .leftJoin(walkingMateState.users,users)
                    .leftJoin(walkingMateState.walkingMate, walkingMate)
                    .where(walkingMate.id.eq(walkmate.getId()).and(walkingMateState.writerCheck.eq(0)))
                    .fetch();
            System.out.println(walkMateStateDto +"실행준");
            List<WalkMateStateDto> walkMateStateDtoList = walkMateStateDto.stream().map(
                    walks -> new WalkMateStateDto(
                            walks.getId(),
                            walks.getWalkMateId(),
                            walks.getUserId(),
                            walks.getUserAccount(),
                            walks.getUserNickName(),
                            walks.getState(),
                            walks.getWriterCheck(),
                            walks.getAddress(),
                            walks.getDetail()
                    )).collect(toList());
            System.out.println(walkMateStateDtoList+"walkMateStateDtoList 뿅");
            return new WalkMateMyDetailListDto(
                    walkmate.getId(),
                    walkmate.getWalkingMateTitle(),
                    walkmate.getWalkingMateContent(),
                    walkmate.getWalkingMateRd(),
                    walkmate.getWalkingMateViewCount(),
                    walkmate.getWalkingMateState(),
                    walkmate.getWalkingMatePerson(),
                    walkmate.getWalkingMateToday(),
                    walkmate.getWalkingMateDate(),
                    walkmate.getWalkingMateTime(),
                    walkmate.getWalkCity(),
                    walkmate.getWalkCounty(),
                    walkmate.getUserId(),
                    walkmate.getUserNickName(),
                    walkmate.getUserAccount(),
                    walkMateStateDtoList
            );
        }).collect(toList());
        result.forEach(r-> System.out.println(r.toString()+"항목입니다."));
        return new PageImpl<>(result, pageable, counts);
    }


    //마이페이지 산책 리스트 확인
    private BooleanExpression createStatusCondition(SearchRecruitmentForm searchRecruitmentForm) {
        System.out.println(searchRecruitmentForm.getState()+"비교");
        try{
            if (searchRecruitmentForm.getState().equals("0")) { // 모집중
                System.out.println("if절 실행");
                return walkingMate.walkingMateState.eq(0L);
            } else if (searchRecruitmentForm.getState().equals("1")) { // 모집완료
                System.out.println("else if절 실행");
                return walkingMate.walkingMateState.eq(1L);
            } else { // 전체보기 ('' 인 경우)
                System.out.println("else 절 실행");
                return null; // 특정 조건 없이 모든 결과 반환
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }

    }

    //마이페이지 내가 신청한 게시글 조회
    @Override
    public Page<WalkMateMyApplicationListDto> findAllWalkMateStateAndUserId(Pageable pageable,SearchRecruitmentForm searchRecruitmentForm,Long userId) {

        List<WalkMateMyApplicationListDto> result = jpaQueryFactory.select(new QWalkMateMyApplicationListDto(
                walkingMate.id,
                walkingMate.walkingMateTitle,
                walkingMate.walkingMateContent,
                walkingMate.walkingMateRd,
                walkingMate.walkingMateViewCount,
                walkingMate.walkingMateState,
                walkingMate.walkingMatePerson,
                walkingMateState.count(),
                walkingMate.walkingMateDate,
                walkingMate.walkingMateTime,
                walkingMate.walkCity,
                walkingMate.walkCounty,
                users.id,
                users.userNickName,
                users.userAccount,
                pet.id,
                petImg.id,
                petImg.petFileName,
                petImg.petPath,
                petImg.petUuid,
                walkingMateState.id,
                walkingMateState.writerCheck
        ))
                .from(walkingMate)
                .leftJoin(walkingMate.users, users)
                .leftJoin(walkingMate.walkingMateStateList, walkingMateState)
                .leftJoin(walkingMate.pet,pet)
                .leftJoin(walkingMate.pet.petImg,petImg)
                .where(
                        walkingMateState.users.id.eq(userId).and(walkingMateState.writerCheck.eq(0)),
                        createStatusCondition(searchRecruitmentForm)

                )
                .groupBy(
                        walkingMate.id,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateContent,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateViewCount,
                        walkingMate.walkingMateState,
                        walkingMate.walkingMatePerson,
                        walkingMate.walkingMateDate,
                        walkingMate.walkingMateTime,
                        walkingMate.walkCity,
                        walkingMate.walkCounty,
                        users.id,
                        users.userNickName,
                        users.userAccount,
                        pet.id,
                        petImg.id,
                        petImg.petFileName,
                        petImg.petPath,
                        petImg.petUuid,
                        walkingMateState.id,
                        walkingMateState.writerCheck

                )
                .orderBy(walkingMate.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long counts =  jpaQueryFactory.select(
                walkingMateState.count()
        )
                .from(walkingMateState)
                .where(
                        createStatusCondition(searchRecruitmentForm),
                        walkingMateState.users.id.eq(userId).and(walkingMateState.writerCheck.eq(0))

                )
                .fetchOne();

        System.out.println(counts+"전체입니다.");
        result.forEach(r-> System.out.println(r.getWalkingMateTitle()+"이고"+r.getWalkingMateStateId()+"입니다."));

        return new PageImpl<>(result, pageable, counts);

    }
}