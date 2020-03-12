package top.xzh.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import top.xzh.seckill.domain.MiaoshaUser;
import top.xzh.seckill.result.Result;
import top.xzh.seckill.common.redis.GoodsKey;
import top.xzh.seckill.service.GoodsService;
import top.xzh.seckill.service.RedisService;
import top.xzh.seckill.vo.GoodsDetailVo;
import top.xzh.seckill.vo.GoodsVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;


@Controller
@Slf4j
@RequestMapping("goods")
public class GoodsController {

    private final GoodsService goodsService;

    private final RedisService redisService;

    private final ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    public GoodsController(GoodsService goodsService, RedisService redisService, ThymeleafViewResolver thymeleafViewResolver) {
        this.goodsService = goodsService;
        this.redisService = redisService;
        this.thymeleafViewResolver = thymeleafViewResolver;
    }

    /**
     * QPS 5335
     */
//    返回格式设置为text/html
    @GetMapping(value = "/to_list", produces = "text/html;charset=UTF-8")
    public @ResponseBody String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoshaUser user) {

        // 1. 取页面缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        // 缓存有数据直接返回
        if (StringUtils.isNotEmpty(html)) {
            return html;
        }

        List <GoodsVo> goodsVos = goodsService.listGoodsVo();

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsVos);

        // 手动渲染 利用Thymeeaf的ThymeleafViewResolver
        // model 就是将参数存入 ，其中的所有参数 都是为了将页面渲染出来 放入其中，在返回一个静态的html源码
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //利用 getTemplateEngine()方法的process() 方法，需要传入模板名称和context 变量
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);

        if (StringUtils.isNotEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    @GetMapping(value = "/detail/{goodsId}")
    public @ResponseBody
    Result<GoodsDetailVo> getDetail(MiaoshaUser user, @PathVariable Long goodsId) {

        // 手动渲染
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        long startTme = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = Instant.now().getEpochSecond() * 1000;

        int miaoshaStatus;
        int remainSeconds;

        // 还没开始
        if (now < startTme) {
            miaoshaStatus = 0;
            remainSeconds = (int) (startTme - now) / 1000;
        // 已结束
        } else if (now > endTime) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo detail = new GoodsDetailVo();
        detail.setGoods(goodsVo);
        detail.setMiaoshaStatus(miaoshaStatus);
        detail.setRemainSeconds(remainSeconds);
        detail.setUser(user);

        return Result.success(detail);
    }

}
