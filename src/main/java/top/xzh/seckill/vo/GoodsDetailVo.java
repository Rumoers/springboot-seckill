package top.xzh.seckill.vo;

import lombok.Data;
import top.xzh.seckill.domain.MiaoshaUser;


@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private MiaoshaUser user;
}
