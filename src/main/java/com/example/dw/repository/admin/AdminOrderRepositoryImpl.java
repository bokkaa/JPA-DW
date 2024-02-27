package com.example.dw.repository.admin;

import com.example.dw.domain.dto.admin.*;
import com.example.dw.domain.dto.admin.orders.AdminOrderList;
import com.example.dw.domain.dto.admin.orders.QAdminOrderList_AdminOrdersListDto;
import com.example.dw.domain.form.AdminSearchOrderForm;
import com.example.dw.domain.form.SearchForm;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.goods.QGoods.goods;
import static com.example.dw.domain.entity.goods.QGoodsMainImg.goodsMainImg;
import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.order.QOrderList.orderList;
import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.user.QUsers.users;
import static java.util.stream.Collectors.groupingBy;

@Repository
@RequiredArgsConstructor
public class AdminOrderRepositoryImpl implements AdminOrderRepositoryCustom{


    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 관리자 페이지 - 주문 목록
     * @param pageable 페이징 처리를 위한 Pageable
     * @param adminSearchOrderForm 검색 요건
     * @return 주문 목록
     */
    @Override
    public Page<AdminOrderList.AdminOrdersListDto.AdminOrderListResultDto> orderLists(
            Pageable pageable, AdminSearchOrderForm adminSearchOrderForm) {

        SearchForm searchForm = new SearchForm(adminSearchOrderForm.getCate(), adminSearchOrderForm.getKeyword());

        //orderListId로 페이징처리하기 위한 루트쿼리
        List<Long> orderListIds = jpaQueryFactory
                .selectDistinct(orderList.id)
                .from(orderList)
                .where(
                        cateKeywordEq(searchForm),
                        dateEq(adminSearchOrderForm.getPrev(), adminSearchOrderForm.getNext())
                )

                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        System.out.println(orderListIds);

        List<AdminOrderList.AdminOrdersListDto> list = jpaQueryFactory.select(new QAdminOrderList_AdminOrdersListDto(
                orderList.id,
                orders.id,
                users.id,
                users.userAccount,
                orders.orderUserAddressNumber,
                orders.orderAddressNormal,
                orders.orderAddressDetails,
                orders.orderUserEmail,
                orders.orderUserName,
                orders.orderUserPhoneNumber,
                orders.orderRegisterDate,
                goods.id,
                goods.goodsName,
                orderItem.orderPrice,
                orderItem.orderQuantity,
                orders.orderRegisterDate
        ))
                .from(orderList)
                .leftJoin(orderList.orders, orders)
                .leftJoin(orders.users, users)
                .leftJoin(orders.orderItems, orderItem)
                .leftJoin(orderItem.goods, goods)
                .where(orderList.id.in(orderListIds))
                .fetch();

        Long getTotal = jpaQueryFactory.select(
                orderList.count()
        )
                .from(orderList)
                .where(
                        cateKeywordEq(searchForm),
                        dateEq(adminSearchOrderForm.getPrev(), adminSearchOrderForm.getNext())
                )
                .fetchOne();

        return new PageImpl<>(convertOrderList(list), pageable, getTotal);
    }

    /**
     * 관리자 페이지 - 주문 상세보기
     * @param userId 회원ID
     * @param orderId 주문ID
     * @return 주문 상세 정보
     */
    @Override
    public AdminOrderDetailResultDto orderDetail(Long userId, Long orderId) {

        //주문 상품 조회
        List<AdminOrderDetailGoodsListDto> adminOrderDetailGoodsList = jpaQueryFactory.select(new QAdminOrderDetailGoodsListDto(
                goods.id,
                goods.goodsName,
                orderItem.orderQuantity,
                orderItem.orderPrice,
                goodsMainImg.goodsMainImgPath,
                goodsMainImg.goodsMainImgUuid,
                goodsMainImg.goodsMainImgName
        ))
                .from(orderItem)
                .leftJoin(orderItem.goods, goods)
                .leftJoin(goods.goodsMainImg, goodsMainImg)
                .where(orders.id.eq(orderId))
                .fetch();

        //주문 기본 정보 조회
        AdminOrderDetailDto adminOrderDetailDto = jpaQueryFactory.select(new QAdminOrderDetailDto(
                orders.users.id,
                orders.users.userAccount,
                orders.orderUserName,
                orders.orderUserEmail,
                orders.orderUserPhoneNumber,
                orders.orderUserAddressNumber,
                orders.orderAddressNormal,
                orders.orderAddressDetails,
                orders.orderMemo,
                orders.orderRegisterDate
                ))
                .from(orders)
                .leftJoin(orders.users, users)
                .leftJoin(orders.orderItems, orderItem)
                .where(orders.users.id.eq(userId).and(orders.id.eq(orderId)))
                .fetchFirst();


    return new AdminOrderDetailResultDto
            (
                  orderId
                    ,
            new AdminOrderDetailDto(

                    adminOrderDetailDto.getUserId(),
                    adminOrderDetailDto.getOrderAccount(),
                    adminOrderDetailDto.getOrderUserName(),
                    adminOrderDetailDto.getOderEmail(),
                    adminOrderDetailDto.getOrderPhone(),
                    adminOrderDetailDto.getOrderZipcode(),
                    adminOrderDetailDto.getOrderAddress(),
                    adminOrderDetailDto.getOrderAddressDetail(),
                    adminOrderDetailDto.getOrderMemo(),
                    adminOrderDetailDto.getOrderDate(),

                    adminOrderDetailGoodsList //주문 상품 List
            ));
    }

    /**
     * 관리자 페이지 - 일별 주문량 조회
     * @return 일별 주문량
     */
    @Override
    public List<AdminWeeklyOrderState> weeklyOrderState() {
        LocalDate nowDate = LocalDate.now();
        LocalDate startWeeklyDateTime = nowDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).minusWeeks(3);

        List<LocalDate> weeklyDates = new ArrayList<>();
        LocalDate current = startWeeklyDateTime;

        // 주간 요일 입력
        while (!current.isAfter(nowDate)) {
            weeklyDates.add(current);
            current = current.plusDays(1);

            System.out.println("요일 : "+current);

        }

        Map<LocalDate, Long> weeklySales = jpaQueryFactory.select(
                orders.orderRegisterDate,
                orders.count()
        )
                .from(orders)
                .where(orders.orderRegisterDate.between(startWeeklyDateTime.atStartOfDay(), nowDate.atTime(23, 59, 59)))
                .orderBy(orders.orderRegisterDate.desc())
                .groupBy(orders.orderRegisterDate)
                .fetch()
                .stream().collect(Collectors.toMap(
                        tuple -> tuple.get(orders.orderRegisterDate).toLocalDate(),
                        tuple -> tuple.get(orders.count()),
                        Long::sum   //같은 키의 값들을 모두 더함 즉, 날짜가 같으면 걍 다 더함
                ));


        for (LocalDate date : weeklyDates) {
            weeklySales.putIfAbsent(date, 0L);
        }

        return weeklySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) //날짜 내림차순 정렬
                .map(sales -> new AdminWeeklyOrderState(sales.getKey(), sales.getValue()))
                .collect(Collectors.toList());
    }


    /**
     * 관리자 페이지 - 상품 카테고리별 판매비율 조회
     * @return 상품 카테고리별 판매량
     */
    @Override
    public List<GoodsSaleByCategory> saleByCategory() {

        List<Tuple> tuples = jpaQueryFactory
                .select(
                        goods.goodsCategory,
                        goods.saleCount.sum().coalesce(0) //null이면 0을 반환
                )
                .from(goods)
                .groupBy(goods.goodsCategory)
                .fetch();

        return tuples.stream()
                .map(tuple ->new GoodsSaleByCategory(
                        tuple.get(goods.goodsCategory),
                        tuple.get(goods.saleCount.sum().coalesce(0))))
                .collect(Collectors.toList());
    }


    /**
     * 관리자 페이지 - 최다 주문 회원 Best5 조회
     * @return 최다 주문 회원 5명 조회
     */
    @Override
    public List<MostOrderUserDto> mostOrders() {

        // 주문 횟수와 총 주문 가격을 구하는 쿼리
        List<Tuple> mostAndTotals = jpaQueryFactory
                .select(
                        users.id,
                        users.userAccount,
                        users.userName,
                        orderItem.orderPrice.multiply(orderItem.orderQuantity).sum().as("totalPrice")
                )
                .from(orderItem)
                .leftJoin(orderItem.orders, orders)
                .leftJoin(orders.users, users)
                .groupBy(users.id,users.userAccount, users.userName)
                .orderBy(orders.count().desc())
                .limit(5)
                .fetch();

        // 사용자별 주문 횟수 리스트
        List<Tuple> most = jpaQueryFactory.select(
                users.id,
                orders.count().as("totalOrderCount")
        )
                .from(orders)
                .leftJoin(orders.users, users)
                .groupBy(users.id)
                .orderBy(orders.count().desc())
                .fetch();

        System.out.println(most.toString()+"!@#!@#");

        return mostAndTotals.stream()
                .map(tuple -> {
                    // most에서 해당 사용자의 주문 횟수를 가져오기
                    Long totalOrderCount = most.stream()
                            .filter(mostTuple -> mostTuple.get(users.id).equals(tuple.get(users.id)))
                            //filter()는 원하는거만 뽑아서 필터링
                            //즉, most의 유저id값과 mostAndTotals의 유저id의 값이 같은 것만 가져옴
                            .findFirst()
                            .map(mostTuple -> mostTuple.get(orders.count().as("totalOrderCount")
                            ))
                            .orElse(0L);

                    return new MostOrderUserDto(
                            tuple.get(users.id),
                            tuple.get(users.userAccount),
                            tuple.get(users.userName),
                            totalOrderCount,
                            tuple.get( orderItem.orderPrice.multiply(orderItem.orderQuantity).sum().as("totalPrice")
                            )
                    );
                })
                .sorted(Comparator.comparing(MostOrderUserDto::getTotalOrderCount, Comparator.reverseOrder())) //order_count기준으로 내림차순 정렬
                .collect(Collectors.toList());
    }


    /**
     * 중복 조회된 정보를 하나로 처리하고 주문ID 내림차순으로 매핑하는 메소드
     * @param orderList orderLists 메소드의 리턴값
     * @return
     */
    private List<AdminOrderList.AdminOrdersListDto.AdminOrderListResultDto> convertOrderList(
            List<AdminOrderList.AdminOrdersListDto> orderList) {


        Map<Long, List<AdminOrderList.AdminOrdersListDto>> groupedOrders =
                orderList.stream()
                .collect(groupingBy(AdminOrderList.AdminOrdersListDto::getOrderListId));



        return groupedOrders.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(entry -> {

                    Long orderListId = entry.getKey();

                    LocalDateTime payDatetime = entry.getValue().stream().findFirst()
                            .map(AdminOrderList.AdminOrdersListDto::getOrderDate)
                            .orElse(null);

                    List<AdminOrderList.AdminOrdersListDto> orderListDtos = entry.getValue();

                    //주문당 구매한 상품은 여러 개일 수 있으므로 구매 상품만 List에 담아준다.
                    List<AdminOrderList.AdminOrdersListDto.AdminOrderItem> adminOrderItems = orderListDtos.stream()
                            .map(dto -> new AdminOrderList.AdminOrdersListDto.AdminOrderItem(
                                    dto.getGoodsId(),
                                    dto.getGoodsName(),
                                    dto.getGoodsPrice(),
                                    dto.getGoodsQuantity()
                            ))
                            .collect(Collectors.toList());

                    AdminOrderList.AdminOrdersListDto.AdminOrderInfo adminOrderInfo = orderListDtos.stream()
                            .findFirst() //중복 조회된 값은 하나로 처리한다.
                            .map(dto -> new AdminOrderList.AdminOrdersListDto.AdminOrderInfo(
                                    dto.getOrderId(),
                                    dto.getUserId(),
                                    dto.getUserAccount(),
                                    dto.getOrderZipcode(),
                                    dto.getOrderAddress(),
                                    dto.getOrderDetailAddress(),
                                    dto.getOrderUserEmail(),
                                    dto.getOrderUserName(),
                                    dto.getOrderUserPhone(),
                                    dto.getOrderDate(),
                                    adminOrderItems //주문한 상품리스트
                            ))
                            .orElse(null);

                    return new AdminOrderList.AdminOrdersListDto.AdminOrderListResultDto(orderListId,payDatetime,adminOrderInfo);
                })
                .collect(Collectors.toList());
    }


    /**
     * 관리자 페이지 - 주문 목록 검색 요건
     * @param searchForm 검색 정보가 들어있는 form
     * @return
     */
    private BooleanExpression cateKeywordEq(SearchForm searchForm){
        if(StringUtils.hasText(searchForm.getCate())&&StringUtils.hasText(searchForm.getKeyword())){

            switch (searchForm.getCate()){

                case "orderNumber" :
                    return orders.id.eq(Long.valueOf(searchForm.getKeyword()));
                case "orderAccount" :
                    return orders.users.userAccount.containsIgnoreCase(searchForm.getKeyword());
                default:
                    break;
            }

        }
        return orders.id.isNotNull();
    }

    /**
     * 관리자 페이지 - 주문 목록 검색(날짜)
     * @param startDate 시작날짜
     * @param endDate 종료날짜
     * @return
     */
    private BooleanExpression dateEq(String startDate, String endDate) {

        //날짜 정보가 입력되지 않은 경우
        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

            //시작날짜와 종료날짜가 서로 뒤바뀐채로 들어온 경우
            //ex) 시작날짜 2023-11-11 / 종료날짜 2023-10-10
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
                return Expressions.allOf(orders.orderRegisterDate.between(start, end));
            } else if (start != null) {
                System.out.println("종료날짜가 없어");
                return Expressions.allOf(orders.orderRegisterDate.after(start));
            } else if (end != null) {
                System.out.println("시작날짜가 없어");
                return Expressions.allOf(orders.orderRegisterDate.before(end));
            }
        } catch (DateTimeException e) {
            e.printStackTrace();
        }

        return null;
    }



}
