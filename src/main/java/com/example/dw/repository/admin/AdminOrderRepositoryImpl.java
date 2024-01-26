package com.example.dw.repository.admin;

import com.example.dw.domain.dto.admin.*;
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

    //관리자 페이지 주문 목록
    @Override
    public Page<AdminOrderListResultDto> orderLists(Pageable pageable, AdminSearchOrderForm adminSearchOrderForm) {
        SearchForm searchForm = new SearchForm(adminSearchOrderForm.getCate(), adminSearchOrderForm.getKeyword());

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

        List<AdminOrderListDto> list = jpaQueryFactory.select(new QAdminOrderListDto(
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

    //관리자 페이지 주문 상세
    @Override
    public AdminOrderDetailResultDto orderDetail(Long userId, Long orderId) {
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
                    adminOrderDetailGoodsList)
            );
    }

    //일별 주문 건수
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
                orders.orderRegisterDate,  // 이 부분을 그대로 사용
                orders.count()
        )
                .from(orders)
                .where(orders.orderRegisterDate.between(startWeeklyDateTime.atStartOfDay(), nowDate.atTime(23, 59, 59)))
                .orderBy(orders.orderRegisterDate.desc())
                .groupBy(orders.orderRegisterDate)  // 이 부분을 변경하지 않음
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
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) //키값 - 날짜 내림차순 정렬
                .map(sales -> new AdminWeeklyOrderState(sales.getKey(), sales.getValue()))
                .collect(Collectors.toList());
    }



    //카테고리별 상품 판매 비율
    @Override
    public List<GoodsSaleByCategory> saleByCategory() {
        //집합함수 결과반환타입은 보통 Tuple
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


    //최다 주문회원 top5
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




    // AdminOrderListDto 목록을 AdminOrderListResultDto로 변환하는 메서드
    private List<AdminOrderListResultDto> convertOrderList(List<AdminOrderListDto> orderList) {

        //.collect stream()을 자료구조로 담을떄 사용
        Map<Long, List<AdminOrderListDto>> groupedOrders = orderList.stream()
                .collect(groupingBy(AdminOrderListDto::getOrderListId));
        //orderListId로 그룹화한다. 즉 Map의 Key값이 orderListId로 들어가게되며
        //value값은 List<>모든 값들이 차례대로 들어간다.


                            //맵을 뜯음                     //키값 기준으로 역순(내림차순으로 정렬)
        return groupedOrders.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(entry -> {
                    Long orderListId = entry.getKey(); //Map의 key값
                    LocalDateTime payDatetime = entry.getValue().stream().findFirst()
                            .map(AdminOrderListDto::getOrderDate)
                            .orElse(null); //findFirst()사용 시 예외처리나 null처리 필수
                    List<AdminOrderListDto> orderListDtos = entry.getValue(); //Map의 value값


                    List<AdminOrderItem> adminOrderItems = orderListDtos.stream()
                            .map(dto -> new AdminOrderItem(
                                    dto.getGoodsId(),
                                    dto.getGoodsName(),
                                    dto.getGoodsPrice(),
                                    dto.getGoodsQuantity()
                            ))
                            .collect(Collectors.toList());

                    System.out.println(adminOrderItems.toString()+"!@@@@@@@@@@@@@");

                    AdminOrderInfo adminOrderInfo = orderListDtos.stream()
                            .findFirst() // 중복되는 주문자 정보가 동일하다면 첫 번째 정보만 가져옴
                            .map(dto -> new AdminOrderInfo(
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
                                    adminOrderItems // 주문 목록을 AdminOrderInfo 안에 포함
                            ))
                            .orElse(null);

                    return new AdminOrderListResultDto(orderListId,payDatetime,adminOrderInfo);
                })
                .collect(Collectors.toList()); //리스트로 변환(Map에서 key값을 버리고 value값으로 리스트로 만든다.)
    }


    //관리자 주문 번호/주문자검색
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

    //날짜 검색
    private BooleanExpression dateEq(String startDate, String endDate) {

        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            System.out.println("둘 다 null이야");
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
