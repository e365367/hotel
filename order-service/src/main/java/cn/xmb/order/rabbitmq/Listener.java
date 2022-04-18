package cn.xmb.order.rabbitmq;

import cn.xmb.order.constatnts.MQConstants;
import cn.xmb.order.pojo.Order;
import cn.xmb.order.service.OrderService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Listener {
    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.ORDER_CREATE_QUEUE),
            exchange = @Exchange(name = MQConstants.ORDER_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MQConstants.ORDER_CREATE_KEY
    ))
    public void orderCreateQueueListener(Order order) {
        System.out.println("接受到消息: "+order);
        orderService.save(order);
    }
}
