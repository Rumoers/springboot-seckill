package top.xzh.seckill.controller;

import cn.hutool.core.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.xzh.seckill.domain.MiaoshaOrder;
import top.xzh.seckill.domain.MiaoshaUser;
import top.xzh.seckill.rabbitmq.MqSender;
import top.xzh.seckill.rabbitmq.dto.MiaoshaMessage;
import top.xzh.seckill.result.CodeMsg;
import top.xzh.seckill.result.Result;
import top.xzh.seckill.access.AccessLimit;
import top.xzh.seckill.common.redis.GoodsKey;
import top.xzh.seckill.service.GoodsService;
import top.xzh.seckill.service.MiaoshaService;
import top.xzh.seckill.service.OrderService;
import top.xzh.seckill.service.RedisService;
import top.xzh.seckill.vo.GoodsVo;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Controller
@RequestMapping("miaosha")
@Slf4j
public class MiaoshaController implements InitializingBean {

    private final GoodsService goodsService;

    private final RedisService redisService;

    private final MiaoshaService miaoshaService;

    private final MqSender sender;

    private final OrderService orderService;

    private static final Map<Long, Boolean> LOCAL_GOODS_MAP = new ConcurrentHashMap <>();
    private Result<Object> error;

    @Autowired
    public MiaoshaController(GoodsService goodsService,
                             RedisService redisService,
                             MiaoshaService miaoshaService,
                             MqSender sender,
                             OrderService orderService) {
        this.goodsService = goodsService;
        this.redisService = redisService;
        this.miaoshaService = miaoshaService;
        this.sender = sender;
        this.orderService = orderService;
    }

    /**
     * 初始化系统时, 将所有商品的库存加入到redis缓存中
     */
    @Override
    public void afterPropertiesSet() {
        //查询库存
        List <GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (null != goodsVos) {
            goodsVos.parallelStream().forEach(goodsVo -> {
                //存放到redis
                redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goodsVo.getId(), goodsVo.getStockCount());
                LOCAL_GOODS_MAP.put(goodsVo.getId(), false);
            });
        }
    }

    /**
     * 2.0 页面静态化处理, 前后端通过json交互
     * 3.0 请求入队,削峰 qps = 2130
     * 4.0 隐藏秒杀地址,秒杀地址超时自动失效
     * 5.0 增加验证码验证
     * qps：2014
     */
    @AccessLimit(seconds = 5, maxCount = 5)
    @PostMapping("/{path}/do_miaosha")
    public @ResponseBody
    Result miaosha(MiaoshaUser user, Long goodsId, @PathVariable String path) {

        // 判断用户是否登录
        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        // 判断秒杀接口,60s会自动失效
        boolean checkMiaoshaPath = miaoshaService.checkMiaoshaPath(user, goodsId, path);
        if (!checkMiaoshaPath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        // 内存标记, 减少redis访问
        if (LOCAL_GOODS_MAP.get(goodsId)) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断库存, 先减一再判断减一后的结果是否大于0
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (null != stock && stock < 0) {
            LOCAL_GOODS_MAP.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断是否已经秒杀过了 （是否重复下单）
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        // 请求入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage(user, goodsId);
        sender.miaoshaSender(miaoshaMessage);

        return Result.success(0);
    }

    /**
     * orderId  秒杀成功
     * -1 秒杀失败
     * 0 排队中
     */
    @GetMapping("result")
    public @ResponseBody Result<Long> miaoshaResult(MiaoshaUser user, Long goodsId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        long orderId = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(orderId);
    }
    /**
     * 获取秒杀地址
     * 防止用户获取token之后不断调用秒杀地址接口
     * 每次获取的url不一样，只有真正点击秒杀才会根据用户id和商品生成对应的秒杀地址
     * */
    @AccessLimit(seconds = 5, maxCount = 5)
    @GetMapping("path")
    public @ResponseBody Result getMiaoshaPath(MiaoshaUser user, Long goodsId, Integer verifyCode) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        // 校验验证码是否正确
        boolean code = miaoshaService.checkMiaoshaVerifyCode(user, goodsId, verifyCode);
        if (!code) {
            return Result.error(CodeMsg.VERIFY_CODE_FAIL);
        }

        // 生成请求地址
        String path = miaoshaService.createMiaoshaPath(user, goodsId);

        return Result.success(path);
    }

    /**
     * 生成验证码, 防恶意刷请求. 5s max request < 50
     */
    @AccessLimit(seconds = 5, maxCount = 50)
    @GetMapping("verifyCode")
    public @ResponseBody Result getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user, Long goodsId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return error;
        }

        BufferedImage bufferedImage = miaoshaService.createMiaoshaVerifyCode(user, goodsId);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, ImageUtil.IMAGE_TYPE_JPEG, outputStream);
            outputStream.flush();
            return null;
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
