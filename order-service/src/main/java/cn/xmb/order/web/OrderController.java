package cn.xmb.order.web;

import cn.xmb.order.pojo.OrderPageResult;
import cn.xmb.order.pojo.Order;
import cn.xmb.order.service.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public void get() {
        System.out.println("get");
    }

    @PostMapping("/create")
    public void createOrder(@RequestBody Order order) {
        orderService.save(order);
    }

    @GetMapping("/pageList")
    public OrderPageResult pageQueryOrder(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        Page<Order> orderPage = orderService.page(new Page<>(page, size));

        return new OrderPageResult(orderPage.getTotal(), orderPage.getRecords());
    }
}
