package cn.xmb.customer.mapper;

import cn.xmb.customer.pojo.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * 客户映射器
 *
 * @author xingmingbao
 * @date 2022/04/15
 */
@Repository
public interface CustomerMapper extends BaseMapper<Customer> {
}
