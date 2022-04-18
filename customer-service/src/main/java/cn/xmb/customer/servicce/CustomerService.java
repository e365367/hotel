package cn.xmb.customer.servicce;

import cn.xmb.customer.pojo.Customer;
import cn.xmb.customer.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * 客户服务
 *
 * @author xingmingbao
 * @date 2022/04/15
 */
public interface CustomerService extends IService<Customer> {


    void creatOrder(Order order);
}
