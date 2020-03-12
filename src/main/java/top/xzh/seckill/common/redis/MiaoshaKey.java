package top.xzh.seckill.common.redis;


public class MiaoshaKey extends AbstractPrefix {
    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static final MiaoshaKey IS_GOODS_OVER = new MiaoshaKey("go");
    public static final MiaoshaKey GET_MIAOSHA_PATH = new MiaoshaKey(60, "gp");
    public static final MiaoshaKey GET_MIAOSHA_VERIFY_CODE = new MiaoshaKey(300, "vc");
}
