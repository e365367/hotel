package cn.xmb.feign.clients;

import cn.xmb.feign.pojo.Hotel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("adminservice")
public interface AdminClient {
    @GetMapping("/hotel/admin/{id}")
    Hotel queryById(@PathVariable Long id);
}
