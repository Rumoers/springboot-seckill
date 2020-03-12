package top.xzh.seckill.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xzh.seckill.domain.MiaoshaUser;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiaoshaMessage {
    private MiaoshaUser user;
    private Long goodsId;
}
