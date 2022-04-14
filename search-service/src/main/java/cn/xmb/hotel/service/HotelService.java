package cn.xmb.hotel.service;

import cn.xmb.feign.pojo.Hotel;
import cn.xmb.hotel.pojo.PageResult;
import cn.xmb.hotel.pojo.RequestParams;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface HotelService extends IService<Hotel> {
    PageResult search(RequestParams requestParams);

    Map<String, List<String>> filters(RequestParams params);

    List<String> suggestion(String prefix);

    void insertOrUpdateById(Long id);

    void deleteById(Long id);
}
