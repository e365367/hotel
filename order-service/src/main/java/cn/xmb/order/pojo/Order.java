package cn.xmb.order.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "tb_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long hotelId;
    private Date createTime;
    private BigDecimal orderPrice;
    private String note;
    @TableField("is_paid")
    private Boolean alreadyPaid;
}
