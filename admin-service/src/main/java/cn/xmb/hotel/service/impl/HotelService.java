package cn.xmb.hotel.service.impl;

import cn.xmb.hotel.constatnts.MqConstatnts;
import cn.xmb.hotel.mapper.HotelMapper;
import cn.xmb.hotel.pojo.Hotel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements cn.xmb.hotel.service.HotelService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendToInsertAndUpdateQueue(Long id) {
        rabbitTemplate.convertAndSend(MqConstatnts.HOTEL_EXCHANGE, MqConstatnts.HOTEL_INSERT_OR_UPDATE_KEY, id);
    }

    @Override
    public void sendToDeleteQueue(Long id) {
        rabbitTemplate.convertAndSend(MqConstatnts.HOTEL_EXCHANGE, MqConstatnts.HOTEL_DELETE_KEY, id);
    }
}
