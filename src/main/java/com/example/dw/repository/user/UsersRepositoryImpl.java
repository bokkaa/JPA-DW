package com.example.dw.repository.user;

import com.example.dw.domain.dto.admin.*;
import com.example.dw.domain.dto.user.QUserPetDto;
import com.example.dw.domain.dto.user.UserPetDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.freeBoard.QFreeBoard.freeBoard;
import static com.example.dw.domain.entity.freeBoard.QFreeBoardComment.freeBoardComment;
import static com.example.dw.domain.entity.goods.QGoods.goods;
import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.question.QQuestion.question;
import static com.example.dw.domain.entity.question.QQuestionComment.questionComment;
import static com.example.dw.domain.entity.user.QPet.pet;
import static com.example.dw.domain.entity.user.QPetImg.petImg;
import static com.example.dw.domain.entity.user.QUserFile.userFile;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.walkingMate.QWalkingMate.walkingMate;
import static com.example.dw.domain.entity.walkingMate.QWalkingMateComment.walkingMateComment;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class UsersRepositoryImpl implements UsersRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;


    /**
     *
     * @param pageable 페이징 처리를 위한 Pageable 변수
     * @param userState 회원 가입 상태(가입 중 / 탈퇴 상태)
     * @param cate 아아디, 이름, 이메일 등 검색 카테고리
     * @param keyword 검색 키워드
     * @return 가입 회원 목록 및 탈퇴 회원 목록
     */
    @Override
    public Page<UserListDto> findByAll(Pageable pageable, String cate, String keyword, String userState) {


        List<UserListDto> userStanInfo = jpaQueryFactory.select(new QUserListDto(
                users.id,
                users.userAccount,
                users.userName,
                users.userEmail,
                users.userPhone,
                users.userJoinDate,
                users.userState,
                jpaQueryFactory.select(
                        question.count()
                )
                        .from(question)
                        .where(question.users.id.eq(users.id)),
                jpaQueryFactory.select(
                        freeBoard.count()
                )
                        .from(freeBoard)
                        .where(freeBoard.users.id.eq(users.id)),
                jpaQueryFactory.select(
                        walkingMate.count()
                )
                        .from(walkingMate)
                        .where(walkingMate.users.id.eq(users.id))

        ))
                .from(users)
                .where(
                        userStateEq(userState),
                        cateEq(cate, keyword)
                )
                .groupBy(users.id, users.userAccount, users.userName, users.userEmail, users.userPhone, users.userJoinDate, users.userState)
                .orderBy(users.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();




         Long counts = jpaQueryFactory.select(users.count())
                 .from(users)
                 .where(
                         userStateEq(userState),
                         cateEq(cate, keyword)
                 )
                 .fetchOne();

        return new PageImpl<>(userStanInfo, pageable, counts);
    }

    /**
     * 회원 상세 정보 가져오기
     * @param userId 회원ID
     * @return 회원 상세 정보
     */
    @Override
    public AdminUserDetailResultDto findByUserId(Long userId) {

        List<AdminUserDetailDto> userDetailInfo = jpaQueryFactory.select(
                new QAdminUserDetailDto(
                        users.id,
                        users.userAccount,
                        users.userName,
                        users.userNickName,
                        users.userPhone,
                        users.userEmail,
                        users.userJoinDate,
                        users.address.zipCode,
                        users.address.address,
                        users.address.detail,
                        users.userIntroduction,
                        userFile.id,
                        userFile.route,
                        userFile.uuid,
                        userFile.name,
                        pet.id,
                        pet.birthDate,
                        pet.name,
                        pet.weight,
                        pet.petGender,
                        pet.neutering,
                        pet.petCategory,
                        petImg.id,
                        petImg.petPath,
                        petImg.petUuid,
                        petImg.petFileName
                )

        )       .from(users)
                .leftJoin(users.userFile, userFile)
                .leftJoin(users.pet, pet)
                .leftJoin(pet.petImg, petImg)
                .where(users.id.eq(userId))
                .fetch();

        return Optional.ofNullable(

                new AdminUserDetailResultDto(

                        userDetailInfo.stream().findFirst().map(
                                o-> new AdminUserDetailInfo(
                                        o.getId(),
                                        o.getUserAccount(),
                                        o.getUserName(),
                                        o.getUserNickName(),
                                        o.getUserPhone(),
                                        o.getUserEmail(),
                                        o.getUserJoinDate(),
                                        o.getZipCode(),
                                        o.getAddress(),
                                        o.getDetail(),
                                        o.getIntro(),
                                        new AdminUserDetailImgDto(
                                                o.getUserImgId(),
                                                o.getUserImgPath(),
                                                o.getUserImgUuid(),
                                                o.getUserImgName()))).get()
                        ,
                        userDetailInfo.stream().collect(
                                mapping(r->new AdminUserPetDetailDto(
                                        r.getPetId(),
                                        r.getBirthDate(),
                                        r.getPetImgName(),
                                        r.getWeight(),
                                        r.getPetGender(),
                                        r.getNeutering(),
                                        r.getPetCategory(),

                                new AdminUserPetImgDto(
                                        r.getPetImgId(),
                                        r.getPetImgPath(),
                                        r.getPetImgUuid(),
                                        r.getPetImgName())),toList()
                        )))
        ).orElseThrow(()->{
           throw new IllegalArgumentException("정보 없음");
        });

    }


    /**
     *
     * @param pageable pageable
     * @param userId 회원ID
     * @return 해당 회원의 주문 내역 및 총 주문 합계 금액
     */
    @Override
    public Page<AdminUserDetailOrderResultWithTotalPriceDto> userPaymentList(Pageable pageable, Long userId) {

        //루트 쿼리 조회
        List<AdminUserDetailOrderDto> content = jpaQueryFactory.select(
                new QAdminUserDetailOrderDto(
                        orders.id,
                        orders.orderRegisterDate
                )
        )
                .from(orders)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orders.id.desc())
                .where(orders.users.id.eq(userId))
                .fetch();


        List<AdminUserDetailOrderResultDto> result =
                content.stream().map(
                        o-> {
                            List<AdminUserDetailPaymentListDto> orderItems = jpaQueryFactory.select(
                                    new QAdminUserDetailPaymentListDto(
                                            orderItem.goods.id,
                                            orderItem.goods.goodsName,
                                            orderItem.orderQuantity,
                                            orderItem.orderPrice
                                    )
                            )
                                    .from(orderItem)
                                    .leftJoin(orderItem.orders, orders)
                                    .leftJoin(orderItem.goods, goods)
                                    .where(orderItem.orders.id.eq(o.getOrderId()))
                                    .fetch();

                            List<AdminUserDetailPaymentListDto> orderItemDtoList =
                                    orderItems.stream().map(
                                            orderItemsDto -> new AdminUserDetailPaymentListDto(
                                                    orderItemsDto.getGoodsId(),
                                                    orderItemsDto.getGoodsName(),
                                                    orderItemsDto.getOrderQuantity(),
                                                    orderItemsDto.getOrderPrice()
                                            )
                                    ).collect(toList());

                            return new AdminUserDetailOrderResultDto(
                                    o.getOrderId(),
                                    o.getOrderRd(),
                                    orderItemDtoList

                            );
                        }
                ).collect(toList());

        Long getTotal = jpaQueryFactory.select(
                orders.count()
        )
                .from(orders)
                .where(orders.users.id.eq(userId))
                .fetchOne();

        Integer totalOrderPrice = jpaQueryFactory.select(
                orderItem.orderQuantity.multiply(orderItem.orderPrice).sum()
        )
                .from(orders)
                .leftJoin(orders.orderItems, orderItem)
                .where(orders.users.id.eq(userId))
                .fetchOne();

        System.out.println("[ 합계금액 ] : " + totalOrderPrice);


        return new PageImpl<>(List.of(new AdminUserDetailOrderResultWithTotalPriceDto(totalOrderPrice, result)
)                   , pageable
                    , getTotal);
    }

//    private List<Long> toOrderIds(List<AdminUserDetailOrderResultDto> rootQuery){
//
//        return rootQuery.stream().map(o->o.getOrderId()).collect(toList());
//    }
//
//    private Map<Long, List<AdminUserDetailPaymentListDto>> findOrderItemMap(List<Long> orderIds){
//
//        List<AdminUserDetailPaymentListDto> orderItems = jpaQueryFactory.select(
//                new QAdminUserDetailPaymentListDto(
//                        orders.id,
//                orderItem.goods.id,
//                orderItem.goods.goodsName,
//                orderItem.orderQuantity,
//                orderItem.orderPrice
//        ))
//                .from(orderItem)
//                .leftJoin(orderItem.orders, orders)
//                .leftJoin(orderItem.goods, goods)
//                .where(orderItem.orders.id.in(orderIds))
//                .fetch();
//
//        return orderItems.stream().collect(Collectors.groupingBy(
//                AdminUserDetailPaymentListDto::getGoodsId
//        ));
//        //키가 orderId, 값은 orderItems
//
//    }


    /**
     *
     * @param userId 회원ID
     * @param pageable Pageable
     * @return 해당 회원이 작성한 QnA 게시글
     */
    @Override
    public Page<AdminUserDetailQnaListDto> userDetailQnaList(Pageable pageable, Long userId) {

        List<AdminUserDetailQnaListDto> qnaListResults =
                jpaQueryFactory.select( new QAdminUserDetailQnaListDto(
                        question.id,
                        question.questionTitle,
                        question.questionRd,
                        question.questionViewCount,
                        questionComment.count()
                ))
                        .from(question)
                        .leftJoin(question.questionComment, questionComment)
                        .where(question.users.id.eq(userId))
                        .orderBy(question.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .groupBy(question.id, question.questionTitle ,question.questionRd, question.questionViewCount)
                        .fetch();


        Long getTotal = jpaQueryFactory.select(
                question.count()
        )
                .from(question)
                .where(question.users.id.eq(userId))
                .fetchOne();

        System.out.println(qnaListResults.toString()+"!@#!@#!@#!@#!@#!@");

        return new PageImpl<>(qnaListResults, pageable, getTotal);
    }

    /**
     *
     * @param userId 회원ID
     * @param pageable Pageable
     * @return 해당 회원이 작성한 자유게시판 게시글
     */
    @Override
    public Page<AdminUserDetailFreeBoardListDto> userDetailFreeBoardList(Pageable pageable, Long userId) {
        List<AdminUserDetailFreeBoardListDto> freeListDtoQueryResults =
                jpaQueryFactory.select(new QAdminUserDetailFreeBoardListDto(
                        freeBoard.id,
                        freeBoard.freeBoardTitle,
                        freeBoard.freeBoardRd,
                        freeBoard.freeBoardViewCount,
                        freeBoardComment.count()
                ))
                    .from(freeBoard)
                    .leftJoin(freeBoard.freeBoardComment, freeBoardComment)
                    .where(freeBoard.users.id.eq(userId))
                        .groupBy(freeBoard.id, freeBoard.freeBoardTitle, freeBoard.freeBoardRd, freeBoard.freeBoardViewCount)
                        .orderBy(freeBoard.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                    .fetch();

        Long getTotal = jpaQueryFactory.select(
                freeBoard.count()
        )
                .from(freeBoard)
                .where(freeBoard.users.id.eq(userId))
                .fetchOne();




        return new PageImpl<>(freeListDtoQueryResults, pageable, getTotal);
    }



    /**
     *
     * @param userId 회원ID
     * @param pageable Pageable
     * @return 해당 회원이 작성한 산책메이트 게시글
     */
    @Override
    public Page<AdminUserDetailWalkMateDto> userDetailWalkMateList(Pageable pageable, Long userId) {

        List<AdminUserDetailWalkMateDto> walkListDtoQueryResults =
                jpaQueryFactory.select(new QAdminUserDetailWalkMateDto(
                        walkingMate.id,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateViewCount,
                        walkingMateComment.count()
                ))
                        .from(walkingMate)
                        .leftJoin(walkingMate.walkingMateComment, walkingMateComment)
                        .where(walkingMate.users.id.eq(userId))
                        .groupBy(walkingMate.id, walkingMate.walkingMateTitle,walkingMate.walkingMateRd, walkingMate.walkingMateViewCount)
                        .orderBy(walkingMate.id.desc())
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();


        Long getTotal = jpaQueryFactory.select(walkingMate.count())
                .from(walkingMate)
                .where(walkingMate.users.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(walkListDtoQueryResults, pageable, getTotal);
    }


    /**
     * 등록된 펫 정보
     * @param userId 회원ID
     * @return 해당 회원ID에 등록된 펫 목록
     */
    @Override
    public List<UserPetDto> findAllPetByUserId(Long userId) {
        return jpaQueryFactory.select(new QUserPetDto(
                pet.id,
                pet.name
        ))
                .from(pet)
                .where(pet.users.id.eq(userId))
                .fetch();
    }


    /**
     *
     * @return 일별 회원 가입 수
     */
    @Override
    public List<AdminUserChartDto> findJoinCountByAll() {

        LocalDate nowDate = LocalDate.now();
        LocalDate startDate = nowDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).atStartOfDay().minusWeeks(3).toLocalDate();

        // 일주일 간의 날짜 목록 생성
        List<LocalDate> datesInRange = startDate.datesUntil(nowDate.plusDays(1))
                .collect(Collectors.toList());

        Map<LocalDate, Long> dailyCounts = jpaQueryFactory
                .select(users.userJoinDate, users.count())
                .from(users)
                .where(users.userJoinDate.between(startDate, nowDate))
                .orderBy(users.userJoinDate.desc())
                .groupBy(users.userJoinDate)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(users.userJoinDate),
                        tuple -> tuple.get(users.count()),
                        Long::sum

                ));

        // 위에서 생성한 일주일 간 날짜를 가져온다.(datesInRange)
        // 빈 값을 가진 날짜에 대한 결과 추가
        for (LocalDate date : datesInRange) {
            dailyCounts.putIfAbsent(date, 0L);
        }

        //메소드의 반환타입인 List<AdminUserChartDto>에 맞추기위해 entrySet().map()을 이용하여 변환한다.
        return dailyCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(entry -> new AdminUserChartDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List> newUserStatus() {
        LocalDate nowDate = LocalDate.now();

        //신규 가입 회원 현황
        List<UserRecentJoinDto> recentJoinDtoList = jpaQueryFactory.select(new QUserRecentJoinDto(
                users.id,
                users.userAccount,
                users.userName,
                users.userEmail,
                users.userPhone,
                users.userJoinDate

        ))
                .from(users)
                .orderBy(users.id.desc())
                .limit(4)
                .where()
                .fetch();

        //전체 가입자 수
        Long totalCount = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .fetchOne();

        //당일 가입 회원 수
        Long joinCount = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .where(users.userJoinDate.eq(nowDate).and(
                        users.userState.eq(1)
                ))
                .fetchOne();

        //당일 탈퇴 회원 수
        Long deleteCountByDay = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .where(users.userDeleteDate.eq(nowDate).and(
                        users.userState.eq(0)
                ))
                .fetchOne();

        //총 탈퇴회원 수
        Long deleteTotalCount = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .where(
                        users.userState.eq(0)
                )
                .fetchOne();

        List<Long> count = new ArrayList<>();
        count.add(totalCount);
        count.add(joinCount);
        count.add(deleteCountByDay);
        count.add(deleteTotalCount);

        Map<String, List> userStats = new HashMap<>();
        userStats.put("userInfoCount", count); //대시 보드 정보
        userStats.put("userRecent", recentJoinDtoList); //최근 가입 회원

        return userStats;
    }


    /**
     *
     * @param userState 회원 상태 ( 0 : 탈퇴 상태 / 1 : 회원 상태 )
     * @return 회원 상태 값
     */
    private BooleanExpression userStateEq(String userState){

        return StringUtils.hasText(userState) ? users.userState.eq(Integer.valueOf(userState)) : null;
    }


    /**
     *
     * @param cate 회원 정보 검색 카테고리
     * @param keyword 회원 정보 검색 키워드
     * @return 각 정보 검색에 맡는 결과 값
     */
    private BooleanExpression cateEq(String cate, String keyword) {
        if (StringUtils.hasText(cate) && StringUtils.hasText(keyword)) {
            switch (cate) {
                case "userAccount":
                    return users.userAccount.containsIgnoreCase(keyword);
                case "userName":
                    return users.userName.containsIgnoreCase(keyword);
                case "userEmail":
                    return users.userEmail.containsIgnoreCase(keyword);
                case "userPhone":
                    return users.userPhone.containsIgnoreCase(keyword);

                default:
                    break;
            }
        }

        return users.id.isNotNull();
    }

    //마이페이지 이동시 회원 정보 가져오기
    @Override
    public Optional<UserDetailListDto> findOneByUserId(Long userId) {
    UserDto content = jpaQueryFactory
            .select(new QUserDto(
                    users.id,
                    users.userAccount,
                    users.userName,
                    users.userNickName,
                    users.userPhone,
                    users.userEmail,
                    users.userJoinDate,
                    users.address.zipCode,
                    users.address.address,
                    users.address.detail,
                    users.userIntroduction
            ))
            .from(users)
            .where(users.id.eq(userId))
            .fetchOne();




    Optional<UserDetailListDto> contents =
            Optional.ofNullable(content).map(userDto -> {
                List<UserFileDto> userFileDto = jpaQueryFactory
                        .select(new QUserFileDto(
                                userFile.id,
                                userFile.route,
                                userFile.name,
                                userFile.uuid,
                                users.id
                        ))
                        .from(userFile)
                        .leftJoin(userFile.users, users)
                        .where(users.id.eq(userDto.getId()))
                        .fetch();

                List<UserFileDto> imgDto = userFileDto.stream()
                        .map(imgDtos -> new UserFileDto(
                                imgDtos.getId(),
                                imgDtos.getRoute(),
                                imgDtos.getName(),
                                imgDtos.getUuid(),
                                imgDtos.getUserId()
                        ))
                        .collect(Collectors.toList());


                return new UserDetailListDto(
                        userDto.getId(),
                        userDto.getUserAccount(),
                        userDto.getUserName(),
                        userDto.getUserNickName(),
                        userDto.getUserPhone(),
                        userDto.getUserEmail(),
                        userDto.getUserJoinDate(),
                        userDto.getZipCode(),
                        userDto.getAddress(),
                        userDto.getDetail(),
                        userDto.getIntro(),
                        imgDto
                );
            });

        System.out.println(contents.toString()+"리스트 ");




        return   contents;
}

// 이미지만 따로 추출


    @Override
    public List<UserFileDto> findAllByUserId(Long userId) {
        return jpaQueryFactory.select(
                new QUserFileDto(
                        userFile.id,
                        userFile.route,
                        userFile.name,
                        userFile.uuid,
                        users.id
                )
        ).from(userFile)
        .leftJoin(userFile.users,users)
        .where(userFile.users.id.eq(userId))
        .fetch();
    }
}
