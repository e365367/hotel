package cn.xmb.order.service.impl;

import cn.xmb.order.mapper.OrderMapper;
import cn.xmb.order.pojo.Order;
import cn.xmb.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    public boolean save(Order order) {
        // 设置订单创建时间
        order.setCreateTime(new Date());
        return super.save(order);
    }
}
