package cn.xmb.order.pojo;

import lombok.Data;

import java.util.List;

@Data
public class OrderPageResult {
    private Long total;
    private List<Order> orderList;

    public OrderPageResult(Long total, List<Order> orderList) {
        this.total = total;
        this.orderList = orderList;
    }
}
