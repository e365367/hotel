package cn.xmb.customer.web;

import cn.xmb.customer.pojo.Customer;
import cn.xmb.customer.pojo.CustomerPageResult;
import cn.xmb.customer.pojo.Order;
import cn.xmb.customer.servicce.CustomerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 客户控制器
 *
 * @author xingmingbao
 * @date 2022/04/18
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/pageList")
    public CustomerPageResult pageQueryCustomer(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Page<Customer> customerPage = customerService.page(new Page<>(page, size));
        return new CustomerPageResult(customerPage.getTotal(), customerPage.getRecords());
    }

    @PostMapping("/createCustomer")
    public void saveCustomer(@RequestBody Customer customer) {
        customerService.save(customer);
        System.out.println("新增用户成功！");
    }

    @PostMapping("/createOrder")
    public void createOrder(
            @RequestBody Order order
    ) {
        customerService.creatOrder(order);
    }

    @PostMapping("/pay")
    public void payOrder(
            @RequestParam("customerId") Long customerId,
            @RequestParam("orderId") Long orderID
    ) {

    }

}
