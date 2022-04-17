package cn.xmb.search.web;

import cn.xmb.search.pojo.PageResult;
import cn.xmb.search.pojo.RequestParams;
import cn.xmb.search.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel/search")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    /**
     * 带条件参数的分页搜索
     *
     * @param requestParams 条件参数
     * @return 返回分页结果
     */
    @PostMapping("/list")
    private PageResult search(@RequestBody RequestParams requestParams) {
        return hotelService.search(requestParams);
    }

    @PostMapping("/filters")
    private Map<String, List<String>> getFilters(@RequestBody RequestParams params) {
        return hotelService.filters(params);
    }

    @GetMapping("/suggestion")
    private List<String> getSuggestion(@RequestParam("key") String prefix){
        return hotelService.suggestion(prefix);
    }
}
