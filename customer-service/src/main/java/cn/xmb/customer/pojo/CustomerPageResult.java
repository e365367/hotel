package cn.xmb.customer.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CustomerPageResult {
    private List<Customer> customerList;
    private Long total;

    public CustomerPageResult() {

    }

    public CustomerPageResult(Long total, List<Customer> customerList) {
        this.total = total;
        this.customerList = customerList;
    }
}
