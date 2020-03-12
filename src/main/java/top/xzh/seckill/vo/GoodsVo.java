package top.xzh.seckill.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.xzh.seckill.domain.Goods;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsVo extends Goods {

    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
