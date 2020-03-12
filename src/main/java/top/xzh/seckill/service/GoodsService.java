package top.xzh.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xzh.seckill.domain.MiaoshaOrder;
import top.xzh.seckill.mapper.GoodsMapper;
import top.xzh.seckill.vo.GoodsVo;

import java.util.List;


@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 获取商品列表
     * */
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 判断库存
     * @param goodsId 商品id
     * @return 是否能够下单
     */
    public boolean checkStockCount(Long goodsId) {
        GoodsVo goodsVo = this.getGoodsVoByGoodsId(goodsId);
        if (null != goodsVo) {
            Integer stockCount = goodsVo.getStockCount();
            return stockCount > 0;
        }
        return false;
    }

    /**
     * 库存 -1
     * @param goodsVo 秒杀商品
     */
    public boolean reduceStock(GoodsVo goodsVo) {
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        int i = goodsMapper.reduceStock(miaoshaOrder);
        return i > 0;
    }
}
