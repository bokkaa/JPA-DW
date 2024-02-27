# SpringBoot-JPA-Project-산책갈개
스프링부트 JPA 프로젝트
<br>


## 🖥️ 프로젝트 소개
강아지 산책 소셜메이트, 쇼핑 플랫폼
<br>


## 🕰️ 개발 기간
* 2023.11 - 2024.01

### 🧑‍🤝‍🧑 맴버구성
 - 팀장: 노의진 : 쇼핑페이지, 장바구니, 주문서 및 결제 페이지, 자유게시판, 공지사항 게시판
 - 팀원1: 복영헌 : 메인페이지(산책글 목록, 주간인기글, 쇼핑 등), 로그인 및 회원가입 등 계정관리 페이지, 산책 메이트 게시판, 관리자페이지
 - 팀원2: 임형준 : 마이페이지, QnA게시판


### ⚙️ 개발 환경
- **IDE** : IntelliJ IDEA
- **Framework** : Springboot(3.x)
- **Database** : Oracle DB(11xe)
- **ORM** : JPA

### 포트폴리오 

[포폴1차.pdf](https://github.com/bokkaa/JPA-DW/files/14395778/1.pdf)

## 📌 내가 맡은 기능
#### 메인 페이지 <a href="https://github.com/bokkaa/JPA-DW/wiki/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80" >상세보기 - WIKI 이동</a>
- 산책메이트 최신 리스트
- 주간 인기 QnA 게시글/ 자유게시판 글
- 카테고리별 상품 리스트
- 최근 본 상품

#### 로그인 / 회원가입 / 계정찾기 <a href="https://github.com/bokkaa/JPA-DW/wiki/%EA%B3%84%EC%A0%95-%EA%B4%80%EB%A0%A8" >상세보기 - WIKI 이동</a>
- 로그인
- 회원가입
- 계정찾기

#### 산책게시글 페이지 <a href="https://github.com/bokkaa/JPA-DW/wiki/%EC%82%B0%EC%B1%85%EB%A9%94%EC%9D%B4%ED%8A%B8-%EA%B2%8C%EC%8B%9C%ED%8C%90" >상세보기 - WIKI 이동</a>
- 산책게시글 목록
- 산책게시글 상세
- 산책게시글 등록
- 산책게시글 수정 및 삭제
  
#### 관리자 페이지 <a href="https://github.com/dafssdf/Spring_Portfoilo/wiki/%EB%A7%88%EC%9D%B4-%ED%8E%98%EC%9D%B4%EC%A7%80" >상세보기 - WIKI 이동</a>
- 회원관리
- 상품관리
- 주문관리
- 커뮤니티 관리

<hr>

## 코드 수정 

<details><summary>1. 무한 증식 DTO</summary>

<img width="467" alt="제목 없음" src="https://github.com/bokkaa/JPA-DW/assets/77730779/8ebbaafe-7333-4178-8bde-49104b332e1c">

- 조회 정보 로직을 주로 담당하는 관리자 페이지에서 조회용 DTO를 마구잡이로 만들다보니 .java 파일 자체가 너무 많아졌다.
- 그래서 이걸 어떻게 하면 가독성도 좋고 유지보수를 쉽게 할 수 있을까 해서 생각해낸 것이 내부 클래스의 활용이었다.
- 내부 클래스를 활용하니 관리자 페이지에서 상품 관련 DTO를 약 12개에서 4개로 줄일 수 있었다.


<img width="352" alt="제목 없음1" src="https://github.com/bokkaa/JPA-DW/assets/77730779/43337bab-5c48-4db5-aab0-2af78ef683af">

<details><summary>Goods/AdminGoods.java </summary>
 
```java
package com.example.dw.domain.dto.admin.goods;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class AdminGoods extends AdminGoodsStan {


    private Long goodsMainImgId;
    private String goodsMainImgPath;
    private String goodsMainImgUuid;
    private String goodsMainImgName;

    private Long goodsDetailImgId;
    private String goodsDetailImgPath;
    private String goodsDetailImgUuid;
    private String goodsDetailImgName;


    @QueryProjection
    public AdminGoods(Long goodsId, String goodsName, String goodsCategory, Integer goodsQuantity, Integer goodsPrice, Integer goodsSaleCount, String goodsDetailContent, String goodsMate, String goodsCertify, LocalDateTime goodsRd, LocalDateTime goodsMd, Long goodsMainImgId, String goodsMainImgPath, String goodsMainImgUuid, String goodsMainImgName, Long goodsDetailImgId, String goodsDetailImgPath, String goodsDetailImgUuid, String goodsDetailImgName) {
        super(goodsId, goodsName, goodsCategory, goodsQuantity, goodsPrice, goodsSaleCount, goodsDetailContent, goodsMate, goodsCertify, goodsRd, goodsMd);
        this.goodsMainImgId = goodsMainImgId;
        this.goodsMainImgPath = goodsMainImgPath;
        this.goodsMainImgUuid = goodsMainImgUuid;
        this.goodsMainImgName = goodsMainImgName;
        this.goodsDetailImgId = goodsDetailImgId;
        this.goodsDetailImgPath = goodsDetailImgPath;
        this.goodsDetailImgUuid = goodsDetailImgUuid;
        this.goodsDetailImgName = goodsDetailImgName;
    }




    //제품 메인 사진
    @Data
    @NoArgsConstructor
    public static class AdminGoodsMainImg {

        private Long goodsMainImgId;
        private String goodsMainImgPath;
        private String goodsMainImgUuid;
        private String goodsMainImgName;


        public AdminGoodsMainImg(Long goodsMainImgId, String goodsMainImgPath, String goodsMainImgUuid, String goodsMainImgName) {
            this.goodsMainImgId = goodsMainImgId;
            this.goodsMainImgPath = goodsMainImgPath;
            this.goodsMainImgUuid = goodsMainImgUuid;
            this.goodsMainImgName = goodsMainImgName;
        }
    }

    //제품 상세 사진
    @Data
    public static class AdminGoodsDetailImg {
        private Long goodsDetailImgId;
        private String goodsDetailImgPath;
        private String goodsDetailImgUuid;
        private String goodsDetailImgName;


        public AdminGoodsDetailImg(Long goodsDetailImgId, String goodsDetailImgPath, String goodsDetailImgUuid, String goodsDetailImgName) {
            this.goodsDetailImgId = goodsDetailImgId;
            this.goodsDetailImgPath = goodsDetailImgPath;
            this.goodsDetailImgUuid = goodsDetailImgUuid;
            this.goodsDetailImgName = goodsDetailImgName;
        }
    }


    //관리자 페이지 상품 리스트
    @Data
    public static class AdminGoodsList {

        private Long goodsId;
        private String goodsCategory;
        private String goodsName;
        private Integer goodsQuantity;
        private Integer goodsSaleCount;
        private Integer goodsPrice;

        private LocalDateTime goodsRd;
        private LocalDateTime goodsMd;

        @QueryProjection
        public AdminGoodsList(Long goodsId, String goodsCategory, String goodsName, Integer goodsQuantity, Integer goodsSaleCount, Integer goodsPrice, LocalDateTime goodsRd, LocalDateTime goodsMd) {
            this.goodsId = goodsId;
            this.goodsCategory = goodsCategory;
            this.goodsName = goodsName;
            this.goodsQuantity = goodsQuantity;
            this.goodsSaleCount = goodsSaleCount;
            this.goodsPrice = goodsPrice;
            this.goodsRd = goodsRd;
            this.goodsMd = goodsMd;
        }
    }


    //관리자 페이지 상품 상세보기
    @Data
    public static class AdminGoodsDetail extends AdminGoodsStan {

        private Double ratingAvg;

        private String goodsMainImgPath;
        private String goodsMainImgUuid;
        private String goodsMainImgName;
        private List<AdminGoods.AdminGoodsDetailImg> adminGoodsDetailImg;

        public AdminGoodsDetail(Long goodsId, String goodsName, String goodsCategory, Integer goodsQuantity, Integer goodsPrice, Integer goodsSaleCount, String goodsDetailContent, String goodsMate, String goodsCertify, LocalDateTime goodsRd, LocalDateTime goodsMd, Double ratingAvg, String goodsMainImgPath, String goodsMainImgUuid, String goodsMainImgName) {
            super(goodsId, goodsName, goodsCategory, goodsQuantity, goodsPrice, goodsSaleCount, goodsDetailContent, goodsMate, goodsCertify, goodsRd, goodsMd);
            this.ratingAvg = ratingAvg;
            this.goodsMainImgPath = goodsMainImgPath;
            this.goodsMainImgUuid = goodsMainImgUuid;
            this.goodsMainImgName = goodsMainImgName;
        }

        public AdminGoodsDetail setGoodsDetailImg(List<AdminGoods.AdminGoodsDetailImg> adminGoodsDetailImg) {
            this.adminGoodsDetailImg = adminGoodsDetailImg;
            return this;
        }

    }


}

```
  
</details>
</details>

<details><summary>2. 페이징 처리 기준</summary>

```java

@Override
    public Page<AdminOrderListResultDto> orderList(Pageable pageable, AdminSearchOrderForm adminSearchOrderForm) {


        SearchForm searchForm = new SearchForm(adminSearchOrderForm.getCate(), adminSearchOrderForm.getKeyword());

        List<AdminOrderListDto> list = jpaQueryFactory.select(new QAdminOrderListDto(
                orderList.id,
                orders.id,
                orders.users.id,
                orders.users.userAccount,
                orders.orderUserAddressNumber,
                orders.orderAddressNormal,
                orders.orderAddressDetail,
                orders.orderUserEmail,
                orders.orderUserName,
                orders.orderUserPhoneNumber,
                orders.orderRegisterDate,
                goods.id,
                goods.goodsName,
                orderItem.orderPrice,
                orderItem.orderQuantity,
                orderList.orderDate
        ))
                .from(orderList)
                .leftJoin(orderList.orders, orders)
                .leftJoin(orders.users, users)
                .leftJoin(orders.orderItems, orderItem)
                .leftJoin(orderItem.goods, goods)
                .where(
                        cateKeywordEq(searchForm),
                        dateEq(adminSearchOrderForm.getPrev(), adminSearchOrderForm.getNext())

                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
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


        return new PageImpl<>(convertOrderList(list),pageable, getTotal);


    }



    // AdminOrderListDto 목록을 AdminOrderListResultDto로 변환하는 메서드
    private List<AdminOrderListResultDto> convertOrderList(List<AdminOrderListDto> orderList) {

        //.collect stream()을 자료구조로 담을떄 사용
        Map<Long, List<AdminOrderListDto>> groupedOrders = orderList.stream()
                .collect(Collectors.groupingBy(AdminOrderListDto::getOrderListId));
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


```

- 페이징 처리 변수를 15로 고정해놓았다. 하지만 위 방식으로 쿼리를 돌려서 뽑아보니
  주문 목록 id 기준으로 페이징처리가 되는 것이 아니라 주문 내역데 들어가 있는 상품들 수로 페이징 기준이 잡혀버렸다.
- 따라서 이를 해결하기 위해 주문ID 조회를 루트로 하는 쿼리를 따로 뽑았고 그 결과값을 본 쿼리의 where in절에 조건으로 넣었다.

<details><summary>수정 코드
</summary>

```java

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

```



</details>

</details>
