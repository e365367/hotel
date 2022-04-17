package cn.xmb.search.mq;

import cn.xmb.search.constants.MqConstatnts;
import cn.xmb.search.service.HotelService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListenner {
    @Autowired
    private HotelService hotelService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstatnts.HOTEL_INSERT_OR_UPDATE_QUEUE),
            exchange = @Exchange(name = MqConstatnts.HOTEL_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstatnts.HOTEL_INSERT_OR_UPDATE_KEY
    ))
    public void insertOrUpdateQueue(Long id) {
        hotelService.insertOrUpdateById(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstatnts.HOTEL_DELETE_QUEUE),
            exchange = @Exchange(name = MqConstatnts.HOTEL_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstatnts.HOTEL_DELETE_KEY
    ))
    public void deleteQueue(Long id) {
        hotelService.deleteById(id);
    }
}

