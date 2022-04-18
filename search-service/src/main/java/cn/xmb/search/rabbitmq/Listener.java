package cn.xmb.search.rabbitmq;

import cn.xmb.search.constants.MQConstatnts;
import cn.xmb.search.service.HotelService;
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
    private HotelService hotelService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstatnts.HOTEL_INSERT_OR_UPDATE_QUEUE),
            exchange = @Exchange(name = MQConstatnts.HOTEL_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MQConstatnts.HOTEL_INSERT_OR_UPDATE_KEY
    ))
    public void insertOrUpdateQueue(Long id) {
        hotelService.insertOrUpdateById(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstatnts.HOTEL_DELETE_QUEUE),
            exchange = @Exchange(name = MQConstatnts.HOTEL_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MQConstatnts.HOTEL_DELETE_KEY
    ))
    public void deleteQueue(Long id) {
        hotelService.deleteById(id);
    }
}

