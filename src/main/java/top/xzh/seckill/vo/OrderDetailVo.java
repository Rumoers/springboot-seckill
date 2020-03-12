package top.xzh.seckill.vo;

import lombok.Data;
import top.xzh.seckill.domain.OrderInfo;


@Data
public class OrderDetailVo {
    private OrderInfo order;
    private GoodsVo goods;
}
