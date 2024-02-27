package com.example.dw.domain.dto.admin.orders;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class AdminOrderList {

    //관리자 주문 리스트
    @Data
    public static class AdminOrdersListDto {

        private Long orderListId;
        private Long orderId;
        private Long userId;
        private String userAccount;
        private String orderZipcode;
        private String orderAddress;
        private String orderDetailAddress;
        private String orderUserEmail;
        private String orderUserName;
        private String orderUserPhone;
        private LocalDateTime orderDate;
        private Long goodsId;
        private String goodsName;
        private Integer goodsPrice;
        private Integer goodsQuantity;
        private LocalDateTime payDatetime;

        @QueryProjection
        public AdminOrdersListDto(Long orderListId, Long orderId, Long userId, String userAccount, String orderZipcode, String orderAddress, String orderDetailAddress,
                                  String orderUserEmail, String orderUserName, String orderUserPhone, LocalDateTime orderDate, Long goodsId,
                                  String goodsName, Integer goodsPrice, Integer goodsQuantity, LocalDateTime payDatetime){

            this.orderListId=orderListId;
            this.orderId=orderId;
            this.userId=userId;
            this.userAccount=userAccount;
            this.orderZipcode=orderZipcode;
            this.orderAddress=orderAddress;
            this.orderDetailAddress=orderDetailAddress;
            this.orderUserEmail=orderUserEmail;
            this.orderUserName=orderUserName;
            this.orderUserPhone=orderUserPhone;
            this.orderDate=orderDate;
            this.goodsId=goodsId;
            this.goodsName=goodsName;
            this.goodsPrice=goodsPrice;
            this.goodsQuantity=goodsQuantity;
            this.payDatetime=payDatetime;

        }


        @Data
        public static class AdminOrderItem {
            private Long goodsId;
            private String goodsName;
            private Integer goodsPrice;
            private Integer goodsQuantity;

            public AdminOrderItem(Long goodsId, String goodsName, Integer goodsPrice, Integer goodsQuantity) {
                this.goodsId = goodsId;
                this.goodsName = goodsName;
                this.goodsPrice = goodsPrice;
                this.goodsQuantity = goodsQuantity;
            }
        }

        @Data
        public static class AdminOrderInfo{

            private Long orderId;
            private Long userId;
            private String userAccount;
            private String orderZipcode;
            private String orderAddress;
            private String orderDetailAddress;
            private String orderUserEmail;
            private String orderUserName;
            private String orderUserPhone;
            private LocalDateTime orderDate;
            private List<AdminOrderItem> adminOrderItems;


            public AdminOrderInfo(Long orderId, Long userId, String userAccount, String orderZipcode, String orderAddress, String orderDetailAddress, String orderUserEmail, String orderUserName, String orderUserPhone, LocalDateTime orderDate, List<AdminOrderItem> adminOrderItems) {
                this.orderId = orderId;
                this.userId = userId;
                this.userAccount = userAccount;
                this.orderZipcode = orderZipcode;
                this.orderAddress = orderAddress;
                this.orderDetailAddress = orderDetailAddress;
                this.orderUserEmail = orderUserEmail;
                this.orderUserName = orderUserName;
                this.orderUserPhone = orderUserPhone;
                this.orderDate = orderDate;
                this.adminOrderItems = adminOrderItems;
            }
        }


        @Data
        public static class AdminOrderListResultDto{

            private Long orderListId;
            private LocalDateTime payDatetime;
            private AdminOrdersListDto.AdminOrderInfo adminOrderInfo;


            public AdminOrderListResultDto(Long orderListId, LocalDateTime payDatetime, AdminOrderInfo adminOrderInfo) {
                this.orderListId = orderListId;
                this.payDatetime = payDatetime;
                this.adminOrderInfo = adminOrderInfo;
            }
        }

    }

}
