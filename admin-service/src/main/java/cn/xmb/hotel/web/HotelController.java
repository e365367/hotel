package cn.xmb.hotel.web;

import cn.xmb.hotel.pojo.Hotel;
import cn.xmb.hotel.pojo.PageResult;
import cn.xmb.hotel.service.HotelService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("hotel/admin")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping("/{id}")
    public Hotel queryById(@PathVariable("id") Long id){
        System.out.println("admin");
        return hotelService.getById(id);
    }

    @GetMapping("/list")
    public PageResult hotelList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "1") Integer size
    ){
        Page<Hotel> result = hotelService.page(new Page<>(page, size));
        System.out.println("adminList");
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @PostMapping
    public void saveHotel(@RequestBody Hotel hotel){
        hotelService.save(hotel);
        // 发送MQ新增消息
        hotelService.sendToInsertAndUpdateQueue(hotel.getId());

    }

    @PutMapping()
    public void updateById(@RequestBody Hotel hotel){
        if (hotel.getId() == null) {
            throw new InvalidParameterException("id不能为空");
        }
        hotelService.updateById(hotel);
        // 发送MQ更新消息
        hotelService.sendToInsertAndUpdateQueue(hotel.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        hotelService.removeById(id);
        // 发送MQ删除消息
        hotelService.sendToDeleteQueue(id);
    }
}
