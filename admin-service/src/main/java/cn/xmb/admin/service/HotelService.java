package cn.xmb.admin.service;

import cn.xmb.admin.pojo.Hotel;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HotelService extends IService<Hotel> {

    void sendToInsertAndUpdateQueue(Long id);

    void sendToDeleteQueue(Long id);
}
