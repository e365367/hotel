package cn.xmb.customer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户
 *
 * @author xingmingbao
 * @date 2022/04/15
 */
@Data
@TableName("tb_customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String sex;
    private BigDecimal money;
}
