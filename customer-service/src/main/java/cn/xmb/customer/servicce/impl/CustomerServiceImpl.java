package cn.xmb.customer.servicce.impl;

import cn.xmb.customer.constatnts.MQConstants;
import cn.xmb.customer.mapper.CustomerMapper;
import cn.xmb.customer.pojo.Customer;
import cn.xmb.customer.pojo.Order;
import cn.xmb.customer.servicce.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 客户服务impl
 *
 * @author xingmingbao
 * @date 2022/04/15
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建订单
     *
     * @param order 订单对象
     */
    @Override
    public void creatOrder(Order order) {
        rabbitTemplate.convertAndSend(MQConstants.ORDER_EXCHANGE,
                MQConstants.ORDER_CREATE_KEY, order);
    }
}
