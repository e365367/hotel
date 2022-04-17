package cn.xmb.search;

import cn.xmb.search.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HotelDemoTest {
    @Autowired
    private HotelService hotelService;

    @Test
    void testFilters() {
//        hotelService.filters();
    }

}
