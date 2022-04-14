package cn.xmb.hotel.service;

import cn.xmb.hotel.pojo.Hotel;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HotelService extends IService<Hotel> {

    void sendToInsertAndUpdateQueue(Long id);

    void sendToDeleteQueue(Long id);
}
