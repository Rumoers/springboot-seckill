package top.xzh.seckill.common.redis;


public class OrderKey extends AbstractPrefix {


    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
