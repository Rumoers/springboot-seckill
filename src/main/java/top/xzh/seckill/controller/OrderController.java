package top.xzh.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.xzh.seckill.domain.MiaoshaUser;
import top.xzh.seckill.domain.OrderInfo;
import top.xzh.seckill.result.CodeMsg;
import top.xzh.seckill.result.Result;
import top.xzh.seckill.service.GoodsService;
import top.xzh.seckill.service.OrderService;
import top.xzh.seckill.vo.GoodsVo;
import top.xzh.seckill.vo.OrderDetailVo;


@RequestMapping("order")
@Controller
public class OrderController {

    private final OrderService orderService;
    private final GoodsService goodsService;

    @Autowired
    public OrderController(OrderService orderService, GoodsService goodsService) {
        this.orderService = orderService;
        this.goodsService = goodsService;
    }

    @GetMapping("detail")
    public @ResponseBody
    Result detail(MiaoshaUser user, Long orderId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 查询order
        OrderInfo orderInfo = orderService.getById(orderId);
        // 查询goods
        if (null == orderInfo) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrder(orderInfo);

        return Result.success(orderDetailVo);
    }

}
